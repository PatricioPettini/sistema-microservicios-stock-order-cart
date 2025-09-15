package com.patomicroservicios.productos_service.service.interfaces;

import com.patomicroservicios.productos_service.dto.request.CategoryCreateDTO;
import com.patomicroservicios.productos_service.dto.request.CategoryUpdateDTO;
import com.patomicroservicios.productos_service.dto.response.CategoryGetDTO;
import com.patomicroservicios.productos_service.model.Category;

import java.util.List;

public interface ICategoryService {
    CategoryGetDTO getCategoryDTO(Long categoryId);
    List<CategoryGetDTO> getAll();
    void deleteCategory(Long categoryId);
    CategoryGetDTO addCategory(CategoryCreateDTO category);
    CategoryGetDTO updateCategory(Long categoryId, CategoryUpdateDTO category);
    Category getCategory(Long categoryId);
}
