package com.patomicroservicios.invoice_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvoiceAlreadyExistsException extends RuntimeException {
    public InvoiceAlreadyExistsException(Long orderId) {
        super("Invoice for order " + orderId + " is already registered");
    }
}
