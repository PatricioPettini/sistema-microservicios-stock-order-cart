package com.patomicroservicios.invoice_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    private String name;
    private String brand;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotalPrice;
}