package com.patomicroservicios.productos_service.dto.request;

// package com.tuapp.dto;

import com.patomicroservicios.productos_service.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProductPatchDTO {
    private String name;
    private Long brandId;
    private Long categoryId;
    private BigDecimal unitPrice;
    private Product.ProductState state;
}
