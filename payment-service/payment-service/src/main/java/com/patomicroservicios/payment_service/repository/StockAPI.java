package com.patomicroservicios.payment_service.repository;

import com.patomicroservicios.payment_service.config.FeignClientConfig;
import com.patomicroservicios.payment_service.config.FeignSecurityConfig;
import com.patomicroservicios.payment_service.dto.request.ProductQuantityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "stock-service", configuration = FeignClientConfig.class)
public interface StockAPI {

    // hacer asincrónico con rabbitmq
    @PutMapping("/api/stock/total/subtract")
    void subtractStock(@RequestBody List<ProductQuantityDTO> productQuantity);

}
