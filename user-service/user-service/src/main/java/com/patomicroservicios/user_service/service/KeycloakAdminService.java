package com.patomicroservicios.user_service.service;

import com.patomicroservicios.user_service.dto.request.RegisterUserRequest;
import com.patomicroservicios.user_service.producer.NotificationProducer;
import com.patomicroservicios.user_service.producer.UserProducer;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakAdminService {

    private final WebClient webClient;
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final boolean legacyAuthPath;

    @Autowired
    UserProducer userPublisher;

    @Autowired
    NotificationProducer notificationProducer;

    private final Keycloak keycloak;

    public KeycloakAdminService(WebClient webClient,
                                @Value("${keycloak.realm}") String realm,
                                @Value("${keycloak.admin.client-id}") String clientId,
                                @Value("${keycloak.admin.client-secret}") String clientSecret,
                                @Value("${keycloak.legacy-auth-path:false}") boolean legacyAuthPath,
                                Keycloak keycloak) {
        this.webClient = webClient;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.legacyAuthPath = legacyAuthPath;
        this.keycloak = keycloak;
    }


    private String tokenEndpoint() {
        // /realms/{realm}/protocol/openid-connect/token  (nuevo)
        // /auth/realms/{realm}/protocol/openid-connect/token  (legacy)
        return (legacyAuthPath ? "/auth" : "") +
                "/realms/" + realm + "/protocol/openid-connect/token";
    }

    private String adminBase() {
        // /admin/realms/{realm}/...
        return (legacyAuthPath ? "/auth" : "") +
                "/admin/realms/" + realm;
    }

    private String getAdminAccessToken() {
        var form = new MultiValueMapAdapter<String, String>(Map.of(
                "grant_type", List.of("client_credentials"),
                "client_id", List.of(clientId),
                "client_secret", List.of(clientSecret)
        ));

        var resp = webClient.post()
                .uri(tokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null || !resp.containsKey("access_token")) {
            throw new IllegalStateException("No se pudo obtener access_token de Keycloak");
        }
        return (String) resp.get("access_token");
    }

    public String registerUser(RegisterUserRequest req) {
        String token = getAdminAccessToken();

        // 1) Crear usuario (sin password)
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", req.getUsername());
        payload.put("email", req.getEmail());
        payload.put("firstName", req.getFirstName());
        payload.put("lastName", req.getLastName());
        payload.put("enabled", true);
        payload.put("emailVerified", false);

        try {
            ResponseEntity<Void> resp = webClient.post()
                    .uri(adminBase() + "/users")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            String userId = null;
            if (resp != null) {
                String location = resp.getHeaders().getFirst(HttpHeaders.LOCATION);
                if (location != null) {
                    userId = location.substring(location.lastIndexOf('/') + 1);
                }
            }

            if (userId == null || userId.isBlank()) {
                // fallback: buscar por username si el Location no viene
                userId = findUserIdByUsername(token, req.getUsername());
                if (userId == null) throw new RuntimeException("No se pudo determinar el ID del usuario creado");
            }

// 2) password, etc...
            setPassword(token, userId, req.getPassword(), false);


            // 3) (Opcional) Enviar verificación de email
            if (req.isSendVerifyEmail()) {
                sendVerifyEmail(token, userId);
            }

            // 4) (Opcional) Asignar un rol de realm (ej: "user")
            assignRealmRole(token, userId, "user");
            userPublisher.sendUserRegistered(userId);
            notificationProducer.sendUserRegisteredEmail(req.getEmail());
            return userId;

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 409) {
                throw new RuntimeException("Usuario ya existe (409)");
            }
            throw new RuntimeException("Error Keycloak: " + e.getStatusCode().value() + " - " + e.getResponseBodyAsString());
        }
    }

    private String findUserIdByUsername(String token, String username) {
        var list = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(adminBase() + "/users")
                        .queryParam("username", username)
                        .queryParam("exact", true)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(List.class)
                .block();

        if (list == null || list.isEmpty()) return null;
        var first = (Map<?, ?>) list.get(0);
        Object id = first.get("id");
        return id != null ? id.toString() : null;
    }

    public Map<String, Object> getUserByUsername(String username) {
        String token = getAdminAccessToken();

        List<Map<String, Object>> result = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(adminBase() + "/users")
                        .queryParam("username", username)
                        .queryParam("exact", true) // opcional: match exacto
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();

        if (result == null || result.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado: " + username);
        }

        // Devolvemos el primer match
        return result.get(0);
    }

    public String getUserEmailById(String userId) {
        String token = getAdminAccessToken();

        Map<String, Object> userData = webClient.get()
                .uri(adminBase() + "/users/{id}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (userData == null || !userData.containsKey("email")) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }

        return (String) userData.get("email");
    }


    private void setPassword(String token, String userId, String password, boolean temporary) {
        Map<String, Object> cred = new HashMap<>();
        cred.put("type", "password");
        cred.put("temporary", temporary);
        cred.put("value", password);

        webClient.put()
                .uri(adminBase() + "/users/{id}/reset-password", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cred)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void sendVerifyEmail(String token, String userId) {
        webClient.put()
                .uri(adminBase() + "/users/{id}/send-verify-email", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // (Opcional) Asignar rol de realm
    public void assignRealmRole(String token, String userId, String roleName) {
        Map role = webClient.get()
                .uri(adminBase() + "/roles/{roleName}", roleName)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (role == null) throw new RuntimeException("Rol no encontrado: " + roleName);

        List<Map> roles = List.of(role);

        webClient.post()
                .uri(adminBase() + "/users/{id}/role-mappings/realm", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roles)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
