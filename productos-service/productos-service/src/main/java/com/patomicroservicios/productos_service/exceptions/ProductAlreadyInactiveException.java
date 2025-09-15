package com.patomicroservicios.productos_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class ProductAlreadyInactiveException extends RuntimeException{
    public ProductAlreadyInactiveException(Long productId) {
        super("Product with id "+ productId +" is already inactive!");
    }
}
