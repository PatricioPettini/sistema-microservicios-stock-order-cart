package com.patomicroservicios.order_service.repository;

import com.patomicroservicios.order_service.config.FeignClientConfig;
import com.patomicroservicios.order_service.dto.response.CartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "carrito-service", configuration = FeignClientConfig.class)
public interface CartAPI {
    @GetMapping("/api/cart/{cartId}")
    CartDTO getCart(@PathVariable Long cartId);
}
