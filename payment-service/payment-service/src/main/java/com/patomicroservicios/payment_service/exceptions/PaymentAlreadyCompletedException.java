package com.patomicroservicios.payment_service.exceptions;

public class PaymentAlreadyCompletedException extends RuntimeException {
    public PaymentAlreadyCompletedException(Long orderId) {
        super("Payment for order " + orderId + " is already completed and cannot be canceled.");
    }
}
