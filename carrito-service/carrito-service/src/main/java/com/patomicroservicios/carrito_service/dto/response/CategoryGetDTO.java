package com.patomicroservicios.carrito_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryGetDTO {
    private Long id;
    private String name;
    private CategoryGetDTO paternCategory;
}