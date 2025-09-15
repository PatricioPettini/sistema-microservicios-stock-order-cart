package com.patomicroservicios.user_service.controller;

import com.patomicroservicios.user_service.dto.request.RegisterUserRequest;
import com.patomicroservicios.user_service.dto.response.RegisterUserResponse;
import com.patomicroservicios.user_service.service.IUserService;
import com.patomicroservicios.user_service.service.KeycloakAdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KeycloakAdminService keycloakAdminService;

    public AuthController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @Autowired
    IUserService userService;

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        String userId = keycloakAdminService.registerUser(request);
        userService.createUser(userId, request);
        return ResponseEntity.status(201).body(new RegisterUserResponse(userId, "User created"));
    }

}
