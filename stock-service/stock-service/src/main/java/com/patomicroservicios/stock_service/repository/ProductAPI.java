package com.patomicroservicios.stock_service.repository;

import com.patomicroservicios.stock_service.config.FeignClientConfig;
import com.patomicroservicios.stock_service.dto.response.ProductGetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "productos-service",
        configuration = FeignClientConfig.class
)public interface ProductAPI {

    @GetMapping("/api/product/{productId}")
    ProductGetDTO getProduct(@PathVariable Long productId);
}
