package com.patomicroservicios.carrito_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProductInCartException extends RuntimeException{
    public ProductInCartException(Long productId) {
        super("Product with Id " + productId + " is already in the cart.");
    }
}
