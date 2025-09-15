package com.patomicroservicios.invoice_service.repository;

import com.patomicroservicios.invoice_service.config.FeignClientConfig;
import com.patomicroservicios.invoice_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignClientConfig.class)
public interface UserAPI {
    @GetMapping("/api/user/{userId}")
    UserDTO getUser(@PathVariable String userId);
}