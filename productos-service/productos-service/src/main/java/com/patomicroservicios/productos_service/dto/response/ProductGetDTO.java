package com.patomicroservicios.productos_service.dto.response;

import com.patomicroservicios.productos_service.model.Product;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductGetDTO {
    private Long productId;
    private String name;
    private String brand;
    private String category;
    private BigDecimal unitPrice;
    private Product.ProductState state;
    private boolean fallback;
}