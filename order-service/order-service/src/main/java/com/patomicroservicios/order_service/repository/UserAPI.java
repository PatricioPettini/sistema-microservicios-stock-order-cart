package com.patomicroservicios.order_service.repository;

import com.patomicroservicios.order_service.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", configuration = FeignClientConfig.class)
public interface UserAPI {
    @GetMapping("/api/user/email/{userId}")
    String getEmailByUserId(@PathVariable String userId);
}