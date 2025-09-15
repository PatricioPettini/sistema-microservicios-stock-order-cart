package com.patomicroservicios.productos_service.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandGetDTO {
    private Long id;
    private String name;
    private boolean fallback;
}