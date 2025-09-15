package com.patomicroservicios.productos_service.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryGetDTO {
    private Long id;
    private String name;
    private CategoryGetDTO parentCategory;
    private boolean fallback;
}