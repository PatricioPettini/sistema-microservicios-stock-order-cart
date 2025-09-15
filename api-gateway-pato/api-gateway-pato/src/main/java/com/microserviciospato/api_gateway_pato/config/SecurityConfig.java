package com.microserviciospato.api_gateway_pato.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Desactiva CSRF
                .authorizeExchange(ex -> ex
                        .anyExchange().permitAll() // Permite todo sin autenticación
                )
                .build();
    }
//    @Bean
//    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(ex -> ex
//                        .pathMatchers("/eureka/**").permitAll()   // rutas públicas
//                        .anyExchange().authenticated()            // el resto exige JWT válido
//                )
//                .oauth2ResourceServer(o -> o.jwt())           // SOLO validar JWT
//                .build();
//    }
}


