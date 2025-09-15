package com.patomicroservicios.productos_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProductAlreadyExistsException extends RuntimeException{
    public ProductAlreadyExistsException(String nombre) {
        super("Producto "+ nombre +" already exists!");
    }
}