package com.patomicroservicios.order_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OrderAlreadyCanceled extends RuntimeException{
    public OrderAlreadyCanceled(Long orderId) {
        super("Order with Id: " + orderId +" is already canceled");
    }
}