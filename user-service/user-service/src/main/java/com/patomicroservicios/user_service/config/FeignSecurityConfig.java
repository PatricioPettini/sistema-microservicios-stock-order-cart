package com.patomicroservicios.user_service.config;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignSecurityConfig {

    @Bean
    public RequestInterceptor bearerAuthRequestInterceptor() {
        return (RequestTemplate template) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                Jwt jwt = jwtAuth.getToken();          // Jwt de Spring Security
                String token = jwt.getTokenValue();
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }

            // Alternativa compatible con otros flows:
            // if (auth instanceof BearerTokenAuthentication bearer) {
            //     template.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer.getToken().getTokenValue());
            // }
        };
    }
}
