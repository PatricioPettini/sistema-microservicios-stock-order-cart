package com.patomicroservicios.stock_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(Long productId) {
        super("There is not enough stock for the product");
    }
}
