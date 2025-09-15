package com.patomicroservicios.carrito_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnregisteredStockException extends RuntimeException{
    public UnregisteredStockException(Long idProducto) {
        super("Stock of product with Id " + idProducto + "isn't registered!");
    }
}
