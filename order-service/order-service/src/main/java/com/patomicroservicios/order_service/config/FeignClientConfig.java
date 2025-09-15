package com.patomicroservicios.order_service.config;

import com.patomicroservicios.order_service.auth.KeycloakTokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig implements RequestInterceptor {

    private final KeycloakTokenService keycloakTokenService;

    public FeignClientConfig(KeycloakTokenService keycloakTokenService) {
        this.keycloakTokenService = keycloakTokenService;
    }

    @Override
    public void apply(RequestTemplate template) {
        String token = keycloakTokenService.getAccessToken();
        template.header("Authorization", "Bearer " + token);
    }
}
