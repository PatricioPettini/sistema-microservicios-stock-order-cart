package com.patomicroservicios.user_service.service;

import com.patomicroservicios.user_service.dto.request.LoginRequest;
import com.patomicroservicios.user_service.dto.request.LogoutRequest;
import com.patomicroservicios.user_service.dto.request.RefreshRequest;
import com.patomicroservicios.user_service.dto.response.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class KeycloakAuthService {

    private final WebClient webClient;
    private final String realm;
    private final String loginClientId;
    private final String loginClientSecret;
    private final boolean loginConfidential;
    private final boolean legacyAuthPath;

    public KeycloakAuthService(
            WebClient keycloakWebClient,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.login.client-id}") String loginClientId,
            @Value("${keycloak.login.client-secret:}") String loginClientSecret,
            @Value("${keycloak.login.confidential:false}") boolean loginConfidential,
            @Value("${keycloak.legacy-auth-path:false}") boolean legacyAuthPath
    ) {
        this.webClient = keycloakWebClient;
        this.realm = realm;
        this.loginClientId = loginClientId;
        this.loginClientSecret = loginClientSecret;
        this.loginConfidential = loginConfidential;
        this.legacyAuthPath = legacyAuthPath;

        // Validación temprana útil
        if (this.loginConfidential && (this.loginClientSecret == null || this.loginClientSecret.isBlank())) {
            throw new IllegalStateException(
                    "keycloak.login.confidential=true pero keycloak.login.client-secret está vacío.");
        }
    }

    private String tokenEndpoint() {
        // moderno: /realms/{realm}/protocol/openid-connect/token
        // legacy : /auth/realms/{realm}/protocol/openid-connect/token
        return (legacyAuthPath ? "/auth" : "") + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    private String logoutEndpoint() {
        return (legacyAuthPath ? "/auth" : "") + "/realms/" + realm + "/protocol/openid-connect/logout";
    }

    /** Intercambia usuario/clave por tokens (password grant). */
    public TokenResponse login(LoginRequest req) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "password");
        form.add("username", req.getUsername());
        form.add("password", req.getPassword());
        form.add("client_id", loginClientId);
        if (loginConfidential) {
            // Enviar el secret en el cuerpo (client_secret_post)
            form.add("client_secret", loginClientSecret);
        }

        Map<String, Object> resp = webClient.post()
                .uri(tokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(WebClientResponseException.class, e ->
                        Mono.error(new RuntimeException("Keycloak login error "
                                + e.getRawStatusCode() + " - " + e.getResponseBodyAsString())))
                .block();

        return mapToTokenResponse(resp, "login");
    }

    /** Usa refresh_token para obtener nuevos tokens. */
    public TokenResponse refresh(RefreshRequest req) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", req.getRefreshToken());
        form.add("client_id", loginClientId);
        if (loginConfidential) {
            form.add("client_secret", loginClientSecret);
        }

        Map<String, Object> resp = webClient.post()
                .uri(tokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(WebClientResponseException.class, e ->
                        Mono.error(new RuntimeException("Keycloak refresh error "
                                + e.getRawStatusCode() + " - " + e.getResponseBodyAsString())))
                .block();

        return mapToTokenResponse(resp, "refresh");
    }

    /** Revoca el refresh_token (cierra sesión de ese token). */
    public void logout(LogoutRequest req) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("client_id", loginClientId);
        if (loginConfidential) {
            form.add("client_secret", loginClientSecret);
        }
        form.add("refresh_token", req.getRefreshToken());

        webClient.post()
                .uri(logoutEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .toBodilessEntity()
                .onErrorResume(WebClientResponseException.class, e ->
                        Mono.error(new RuntimeException("Keycloak logout error "
                                + e.getRawStatusCode() + " - " + e.getResponseBodyAsString())))
                .block();
    }

    // -------- helpers --------

    @SuppressWarnings("unchecked")
    private TokenResponse mapToTokenResponse(Map<String, Object> resp, String context) {
        if (resp == null || !resp.containsKey("access_token")) {
            throw new RuntimeException("Keycloak no devolvió access_token (" + context + ")");
        }
        var access = (String) resp.get("access_token");
        var refresh = (String) resp.get("refresh_token");
        var expiresIn = ((Number) resp.get("expires_in")).longValue();
        var refreshExpiresIn = ((Number) resp.get("refresh_expires_in")).longValue();
        var tokenType = (String) resp.getOrDefault("token_type", "Bearer");
        var scope = (String) resp.getOrDefault("scope", "");

        return new TokenResponse(access, refresh, expiresIn, refreshExpiresIn, tokenType, scope);
    }
}
