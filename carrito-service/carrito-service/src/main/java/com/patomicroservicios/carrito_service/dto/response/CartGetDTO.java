package com.patomicroservicios.carrito_service.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartGetDTO {
    private Long id;
    private String userId;
    private BigDecimal subtotalPrice;
    private List<ProductGetDTO> productList;
    private boolean fallback;
}
