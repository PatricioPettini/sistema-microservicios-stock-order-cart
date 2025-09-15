package com.patomicroservicios.productos_service.dto.request;

import com.patomicroservicios.productos_service.dto.response.CategoryGetDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateDTO {
    @NotBlank(message = "name cannot be blank")
    private String name;
    private CategoryGetDTO parentCategory;
}
