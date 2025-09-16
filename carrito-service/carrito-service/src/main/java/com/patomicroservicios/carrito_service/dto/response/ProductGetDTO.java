package com.patomicroservicios.carrito_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductGetDTO {
    private Long productId;
    private String name;
    private String brand;
    private BigDecimal unitPrice;
    private int quantity;
    private String state;
    private boolean fallback;
}
