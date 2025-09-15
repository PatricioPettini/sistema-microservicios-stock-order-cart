package com.patomicroservicios.carrito_service.exceptions;


public class CartNotFoundException extends RuntimeException{
    public CartNotFoundException(Long cartId) {
        super("cart " + cartId + " wasn't found!");
    }
}
