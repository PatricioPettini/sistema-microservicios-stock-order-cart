package com.patomicroservicios.stock_service.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockGetDTO {
    private Long id;
    private Long productId;
    private int totalQuantity;
    private int reservedQuantity;
    private boolean fallback;
}
