package com.patomicroservicios.productos_service.controller;

import com.patomicroservicios.productos_service.dto.request.BrandCreateDTO;
import com.patomicroservicios.productos_service.dto.request.BrandUpdateDTO;
import com.patomicroservicios.productos_service.dto.response.BrandGetDTO;
import com.patomicroservicios.productos_service.service.interfaces.IBrandService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandController {

    @Autowired
    IBrandService brandService;

    @Operation(
            summary = "Get brand by ID",
            description = "Returns a single brand identified by its unique ID."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<BrandGetDTO> getBrand(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getBrandDTO(id));
    }

    @Operation(
            summary = "List all brands",
            description = "Returns the complete list of brands."
    )
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<BrandGetDTO>> getAll() {
        List<BrandGetDTO> brandList = brandService.getAll();
        return ResponseEntity.ok(brandList);
    }

    @Operation(
            summary = "Create brand",
            description = "Creates a new brand and returns the created resource."
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandGetDTO> addBrand(@RequestBody BrandCreateDTO brandDto) {
        BrandGetDTO mar=brandService.addBrand(brandDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mar);
    }

    @Operation(
            summary = "Update brand",
            description = "Updates an existing brand identified by ID and returns the updated resource."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandGetDTO> putBrand(@PathVariable Long id, @RequestBody BrandUpdateDTO brandDto) {
        BrandGetDTO mar=brandService.updateBrand(id, brandDto);
        return ResponseEntity.ok(mar);
    }

    @Operation(
            summary = "Delete brand",
            description = "Deletes a brand by its ID."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Brand deleted succesfully!");
    }
}
