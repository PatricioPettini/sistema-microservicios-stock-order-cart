package com.patomicroservicios.user_service.controller;
import com.patomicroservicios.user_service.dto.request.LoginRequest;
import com.patomicroservicios.user_service.dto.request.LogoutRequest;
import com.patomicroservicios.user_service.dto.request.RefreshRequest;
import com.patomicroservicios.user_service.dto.response.TokenResponse;
import com.patomicroservicios.user_service.service.KeycloakAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {

    private final KeycloakAuthService authService;

    public LoginController(KeycloakAuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")

    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenResponse tokens = authService.refresh(request);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("permitAll()")
    public void logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request); // o request.getRefreshToken() si elegiste esa firma
    }
}
