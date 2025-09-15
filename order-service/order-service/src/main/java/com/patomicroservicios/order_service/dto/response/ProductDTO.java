package com.patomicroservicios.order_service.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductDTO {
    private Long productId;
    private String name;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotalPrice;
}
