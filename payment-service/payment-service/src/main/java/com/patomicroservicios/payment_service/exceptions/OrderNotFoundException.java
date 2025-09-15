package com.patomicroservicios.payment_service.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("Order with Id " + orderId + " wasn't found.");
    }
}
