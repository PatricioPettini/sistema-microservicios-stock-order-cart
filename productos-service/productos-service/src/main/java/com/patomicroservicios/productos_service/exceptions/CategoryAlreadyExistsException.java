package com.patomicroservicios.productos_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryAlreadyExistsException extends RuntimeException{
    public CategoryAlreadyExistsException(String name) {
        super("Category "+ name + " already exists!");
    }
}
