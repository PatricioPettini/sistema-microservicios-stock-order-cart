package com.patomicroservicios.productos_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class BrandAlreadyExistsException extends RuntimeException{
    public BrandAlreadyExistsException(String name) {
        super("Brand "+ name + " already exists!");
    }
}
