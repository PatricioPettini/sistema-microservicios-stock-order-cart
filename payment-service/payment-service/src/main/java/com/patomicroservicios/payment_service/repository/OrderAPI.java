package com.patomicroservicios.payment_service.repository;

import com.patomicroservicios.payment_service.config.FeignClientConfig;
import com.patomicroservicios.payment_service.config.FeignSecurityConfig;
import com.patomicroservicios.payment_service.dto.response.OrderGetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "order-service", configuration = FeignClientConfig.class)
public interface OrderAPI {
    @GetMapping("/api/order/{orderId}")
    OrderGetDTO getOrder(@PathVariable Long orderId);
}
