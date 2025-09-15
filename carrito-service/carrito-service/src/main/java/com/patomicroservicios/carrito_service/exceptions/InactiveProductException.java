package com.patomicroservicios.carrito_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InactiveProductException extends RuntimeException{
    public InactiveProductException(Long productId) {
        super("Product with Id: " + productId +" isn't active!");
    }
}
