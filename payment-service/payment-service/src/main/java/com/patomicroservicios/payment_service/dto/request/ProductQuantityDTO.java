package com.patomicroservicios.payment_service.dto.request;

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
