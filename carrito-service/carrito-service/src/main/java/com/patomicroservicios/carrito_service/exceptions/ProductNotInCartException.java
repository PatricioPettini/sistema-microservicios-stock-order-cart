package com.patomicroservicios.carrito_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotInCartException extends RuntimeException{
    public ProductNotInCartException(Long cartId, Long productId) {
        super("Product with Id " + productId + " was not found in cart " + cartId + ".");
    }
}
