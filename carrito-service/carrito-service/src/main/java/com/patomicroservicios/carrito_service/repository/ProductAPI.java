package com.patomicroservicios.carrito_service.repository;

import com.patomicroservicios.carrito_service.config.FeignClientConfig;
import com.patomicroservicios.carrito_service.dto.response.ProductGetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "productos-service", configuration = FeignClientConfig.class)
public interface ProductAPI {

    @GetMapping("/api/product/{productId}")
    ProductGetDTO getProduct(@PathVariable("productId") Long productId);

    @GetMapping("/api/product/by-ids")
    List<ProductGetDTO> getProductsByIds(@RequestParam("ids") List<Long> productIds);

}
