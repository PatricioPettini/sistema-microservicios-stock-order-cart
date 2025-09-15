package com.patomicroservicios.stock_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StockNotRegisteredException extends RuntimeException{
    public StockNotRegisteredException(Long productId) {
        super("Stock of product with ID " + productId + " is not registered.");
    }
}
