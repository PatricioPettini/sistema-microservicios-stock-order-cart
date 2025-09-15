package com.patomicroservicios.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 1, max = 60)
    private String firstName;

    @NotBlank @Size(min = 1, max = 60)
    private String lastName;

    @NotBlank @Size(min = 6, max = 100)
    private String password;

    @NotBlank @Size(min = 6, max = 8)
    private String dni;

    // Opcionales
    private boolean emailVerified = false;
    private boolean sendVerifyEmail = false; // requiere SMTP configurado

    // getters/setters
    // (o lombok @Data)
    // ...
}
