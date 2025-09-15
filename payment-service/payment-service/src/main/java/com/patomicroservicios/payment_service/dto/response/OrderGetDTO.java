package com.patomicroservicios.payment_service.dto.response;

import com.patomicroservicios.payment_service.dto.ProductDTO;
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
    private String status;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private boolean fallback;
}