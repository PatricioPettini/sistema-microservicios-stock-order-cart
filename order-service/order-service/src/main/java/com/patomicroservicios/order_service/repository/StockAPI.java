package com.patomicroservicios.order_service.repository;

import com.patomicroservicios.order_service.config.FeignClientConfig;
import com.patomicroservicios.order_service.dto.request.ProductQuantityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "stock-service", configuration = FeignClientConfig.class)
public interface StockAPI {
    @PutMapping("/api/stock/reserved/subtract")
    void restoreReservedStock(@RequestBody List<ProductQuantityDTO> dtoList);

    @PutMapping("/api/stock/reserved/add")
    void reserveStock(@RequestBody ProductQuantityDTO productQuantity);
}