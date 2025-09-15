package com.patomicroservicios.invoice_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderGetDTO {
    private Long id;
    private String userId;
    private List<ProductDTO> items = new ArrayList<>();
    private BigDecimal subtotalPrice = BigDecimal.ZERO;
    private BigDecimal taxes = BigDecimal.ZERO;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private boolean fallback;
}