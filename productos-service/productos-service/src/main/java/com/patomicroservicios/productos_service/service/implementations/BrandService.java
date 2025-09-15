package com.patomicroservicios.productos_service.service.implementations;

import com.patomicroservicios.productos_service.exceptions.BrandNotFoundException;
import com.patomicroservicios.productos_service.exceptions.BrandAlreadyExistsException;
import com.patomicroservicios.productos_service.dto.request.BrandCreateDTO;
import com.patomicroservicios.productos_service.dto.request.BrandUpdateDTO;
import com.patomicroservicios.productos_service.dto.response.BrandGetDTO;
import com.patomicroservicios.productos_service.model.Brand;
import com.patomicroservicios.productos_service.repository.IBrandRepository;
import com.patomicroservicios.productos_service.service.interfaces.IBrandService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.List;

@Service
public class BrandService implements IBrandService {
    @Autowired
    IBrandRepository brandRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackBrandNotFound")
    @Retry(name = "retryGetBrandDTO")
    public BrandGetDTO getBrandDTO(Long brandId) {
        return toDto(getBrand(brandId));
    }

    public BrandGetDTO fallbackBrandNotFound(Long brandId, Throwable throwable) {

        return BrandGetDTO.builder()
                .id(brandId)
                .name("FAILED")
                .fallback(true)
                .build();
    }

    @Override
    public List<BrandGetDTO> getAll() {
        return brandRepository.findAll().stream()
                .map(this::toDto)   // acá usamos ModelMapper
                .toList();
    }

    public BrandGetDTO toDto(Brand brand){
        return modelMapper.map(brand, BrandGetDTO.class);
    }

    @Override
    public BrandGetDTO addBrand(BrandCreateDTO brand) {
        String name=brand.getName().trim();

        // validate if brand name already exists
        validateBrandName(name, null);

        Brand brand1= Brand.builder()
                .name(name)
                .build();

        return saveAndReturn(brand1);
    }

    private void validateBrandName(String name, Long brandId) {
        brandRepository.getByName(name)
                .filter(existingBrand -> !existingBrand.getId().equals(brandId)) // si estoy editando, evitar choque con sí misma
                .ifPresent(b -> {
                    throw new BrandAlreadyExistsException(name);
                });
    }

    @Override
    public void deleteBrand(Long brandId) {
        Brand brand= getBrand(brandId);
        brandRepository.deleteById(brand.getId());
    }

    @Override
    @Transactional
    public BrandGetDTO updateBrand(Long brandId, BrandUpdateDTO brand) {
        // get brand
        Brand brand1= getBrand(brandId);
        // get brand name
        String name=brand.getName().trim();

        // validate if brand name already exists
        validateBrandName(name, brandId);

        brand1.setName(brand.getName());

        return saveAndReturn(brand1);
    }

    private BrandGetDTO saveAndReturn(Brand brand1) {
        //save brand
        Brand saved=brandRepository.save(brand1);
        //return dto
        return toDto(saved);
    }

    @Override
    public Brand getBrand(Long brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(()->new BrandNotFoundException(brandId));
    }
}
