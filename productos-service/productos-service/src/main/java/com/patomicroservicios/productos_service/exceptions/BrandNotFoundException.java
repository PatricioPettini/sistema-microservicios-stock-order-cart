package com.patomicroservicios.productos_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class BrandNotFoundException extends RuntimeException{
    public BrandNotFoundException(Long brandId) {
        super("La marca con id "+ brandId + " no fue encontrada!");
    }
}
