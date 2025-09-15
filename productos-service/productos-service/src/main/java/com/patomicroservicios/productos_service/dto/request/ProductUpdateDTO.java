package com.patomicroservicios.productos_service.dto.request;

import com.patomicroservicios.productos_service.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDTO {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Brand ID cannot be null")
    @Positive
    private Long brandId;

    @NotNull(message = "Product type ID cannot be null")
    @Positive
    private Long categoryId;

    @Positive(message = "Price must be greater than zero")
    @NotNull(message = "Prize cannot be null")
    private BigDecimal unitPrice;

    @NotNull(message = "Status cannot be null")
    private Product.ProductState state;
}