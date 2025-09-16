package com.patomicroservicios.order_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OrderAlreadyPaid extends RuntimeException{
    public OrderAlreadyPaid(Long orderId) {
        super("Order with Id: " + orderId +" is already paid");
    }
}