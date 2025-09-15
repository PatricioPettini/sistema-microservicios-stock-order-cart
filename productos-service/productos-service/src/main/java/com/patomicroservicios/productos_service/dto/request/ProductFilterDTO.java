package com.patomicroservicios.productos_service.dto.request;

import com.patomicroservicios.productos_service.model.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterDTO {
    private Long brandId;
    private Long categoryId;
    private Product.ProductState state;
}