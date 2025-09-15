package com.patomicroservicios.invoice_service.repository;

import com.patomicroservicios.invoice_service.config.FeignClientConfig;
import com.patomicroservicios.invoice_service.dto.OrderGetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", configuration = FeignClientConfig.class)
public interface OrderAPI {
    @GetMapping("/api/order/{orderId}")
    OrderGetDTO getOrder(@PathVariable Long orderId);
}