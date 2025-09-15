package com.patomicroservicios.order_service.dto.response;

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
    private String code;
    private String userId;
    private String status;
    private List<ProductDTO> items = new ArrayList<>();
    private BigDecimal subtotalPrice = BigDecimal.ZERO;
    private BigDecimal taxes = BigDecimal.ZERO;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private boolean fallback;
}