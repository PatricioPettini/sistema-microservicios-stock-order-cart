package com.patomicroservicios.order_service.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDTO {
    private Long id;
    private String userId;
    private BigDecimal subtotalPrice;
    private List<ProductDTO> productList;
    private boolean fallback;
}
