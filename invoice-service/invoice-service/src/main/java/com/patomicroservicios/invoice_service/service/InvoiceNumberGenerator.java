package com.patomicroservicios.invoice_service.service;

import org.springframework.stereotype.Component;

@Component
public class InvoiceNumberGenerator {
    public String generate(Long orderId) {
        return "FAC-" + String.format("%06d", orderId);
    }
}
