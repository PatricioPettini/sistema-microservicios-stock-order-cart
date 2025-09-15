package com.patomicroservicios.invoice_service.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceGetDTO {
    private Long id;
    private String invoiceNumber;

    @Column(nullable = false)
    private String userId; //client

    @Column(nullable = false)
    private BigDecimal subtotalPrice;

    @Column(nullable = false)
    private BigDecimal taxes;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    private List<ProductDTO> details;
}
