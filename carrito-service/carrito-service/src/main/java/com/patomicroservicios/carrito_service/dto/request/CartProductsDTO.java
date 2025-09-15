package com.patomicroservicios.carrito_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartProductsDTO {
    private Long cartId;
    private Long productId;
    private int quantity;
}
