package com.patomicroservicios.productos_service.controller;

import com.patomicroservicios.productos_service.dto.request.CategoryCreateDTO;
import com.patomicroservicios.productos_service.dto.request.CategoryUpdateDTO;
import com.patomicroservicios.productos_service.dto.response.CategoryGetDTO;
import com.patomicroservicios.productos_service.service.interfaces.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    ICategoryService categoryService;

    @Operation(
            summary = "Retrieve a category by ID",
            description = "Returns a single category resource identified by its ID"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<CategoryGetDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryDTO(id));
    }

    @Operation(
            summary = "Retrieve all categories",
            description = "Returns a list of all existing categories"
    )
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<CategoryGetDTO>> getAllCategories() {
        List<CategoryGetDTO> categories = categoryService.getAll();
        return ResponseEntity.ok(categories);
    }

    @Operation(
            summary = "Create a new category",
            description = "Creates and returns a new category based on the provided data"
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryGetDTO> addCategory(@RequestBody CategoryCreateDTO category) {
        CategoryGetDTO created = categoryService.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Update an existing category",
            description = "Modifies the details of a category identified by its ID"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryGetDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryUpdateDTO category) {
        CategoryGetDTO updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete a category",
            description = "Deletes the category identified by its ID"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category successfully deleted");
    }



}
