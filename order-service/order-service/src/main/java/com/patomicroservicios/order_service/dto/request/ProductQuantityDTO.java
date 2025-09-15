package com.patomicroservicios.order_service.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductQuantityDTO {
    private Long productId;
    private int quantity;
}
