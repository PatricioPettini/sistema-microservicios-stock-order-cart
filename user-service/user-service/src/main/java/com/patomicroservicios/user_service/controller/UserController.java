package com.patomicroservicios.user_service.controller;

import com.patomicroservicios.user_service.model.User;
import com.patomicroservicios.user_service.service.IUserService;
import com.patomicroservicios.user_service.service.KeycloakAdminService;
import com.patomicroservicios.user_service.service.KeycloakAuthService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final KeycloakAdminService keycloakAdminService;

    public UserController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @Autowired
    IUserService userService;

    @PermitAll // lo uso para saber el id de un usuario para pruebas
    @GetMapping("username/{username}")
    public ResponseEntity<Map<String, Object>> getUserId(@PathVariable String username) {
        Map<String, Object> user = keycloakAdminService.getUserByUsername(username);

        Map<String, Object> response = Map.of(
                "id", user.get("id"),
                "email", user.get("email")
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @GetMapping("/email/{userId}")
    public ResponseEntity<String> getEmail(@PathVariable String userId) {
        String email = keycloakAdminService.getUserEmailById(userId);
        return ResponseEntity.ok(email);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }
}
