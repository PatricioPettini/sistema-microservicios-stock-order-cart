package com.patomicroservicios.carrito_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StockQuantityException extends RuntimeException{
    public StockQuantityException(Long productId) {
        super("Not enough stock available for product with Id " + productId + ".");
    }
}
