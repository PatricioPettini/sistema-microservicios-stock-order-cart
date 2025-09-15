package com.patomicroservicios.productos_service.service.interfaces;

import com.patomicroservicios.productos_service.dto.request.BrandCreateDTO;
import com.patomicroservicios.productos_service.dto.request.BrandUpdateDTO;
import com.patomicroservicios.productos_service.dto.response.BrandGetDTO;
import com.patomicroservicios.productos_service.model.Brand;

import java.util.List;

public interface IBrandService {
    BrandGetDTO getBrandDTO(Long id);
    List<BrandGetDTO> getAll();
    BrandGetDTO addBrand(BrandCreateDTO brand);
    void deleteBrand(Long id);
    BrandGetDTO updateBrand(Long id, BrandUpdateDTO brand);
    Brand getBrand(Long id);
}
