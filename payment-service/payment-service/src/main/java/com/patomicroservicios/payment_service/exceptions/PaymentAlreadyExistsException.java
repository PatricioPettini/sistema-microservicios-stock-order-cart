package com.patomicroservicios.payment_service.exceptions;

public class PaymentAlreadyExistsException extends RuntimeException {
    public PaymentAlreadyExistsException(Long orderId) {
        super("Payment for order " + orderId + " is already registered");
    }
}
