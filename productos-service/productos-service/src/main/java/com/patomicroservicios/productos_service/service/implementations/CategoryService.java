package com.patomicroservicios.productos_service.service.implementations;

import com.patomicroservicios.productos_service.exceptions.CategoryNotFoundException;
import com.patomicroservicios.productos_service.exceptions.CategoryAlreadyExistsException;
import com.patomicroservicios.productos_service.dto.request.CategoryCreateDTO;
import com.patomicroservicios.productos_service.dto.request.CategoryUpdateDTO;
import com.patomicroservicios.productos_service.dto.response.CategoryGetDTO;
import com.patomicroservicios.productos_service.model.Category;
import com.patomicroservicios.productos_service.repository.ICategoryRepository;
import com.patomicroservicios.productos_service.service.interfaces.ICategoryService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    ICategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackCategoryNotFound")
    @Retry(name = "retryGetCategoryDTO")
    public CategoryGetDTO getCategoryDTO(Long categoryId){
        return toDto(this.getCategory(categoryId));
    }

    public CategoryGetDTO fallbackCategoryNotFound(Long categoryId, Throwable throwable) {

        return CategoryGetDTO.builder()
                .id(categoryId)
                .name("FAILED")
                .parentCategory(null)
                .fallback(true)
                .build();
    }

    @Override
    public List<CategoryGetDTO> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)   // acá uso ModelMapper
                .toList();
    }

    public CategoryGetDTO toDto(Category category){
        return modelMapper.map(category, CategoryGetDTO.class);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category cat= this.getCategory(categoryId);
        categoryRepository.deleteById(cat.getId());
    }

    @Override
    @Transactional
    public CategoryGetDTO addCategory(CategoryCreateDTO dto) {

        //validate dto name
        String name = validateName(dto.getName());

        //validate if category name already exists
        validateCategoryNameUnique(dto.getName(), null);

        Category parent;
        parent=getCategory(dto.getParentCategory().getId());

        //create new category and set fields
        Category category= new Category();
        category.setName(name);
        category.setParentCategory(parent);

        //save category and return dto
        return saveAndReturn(category);
    }

    private CategoryGetDTO saveAndReturn(Category category) {
        Category savedCategory = categoryRepository.save(category);
        return toDto(savedCategory);
    }

    private static String validateName(String dto) {
        return Optional.ofNullable(dto)
                .map(String::trim)
                .orElseThrow(() -> new IllegalArgumentException("The name is required"));
    }

    private void validateCategoryNameUnique(String name, Long categoryId) {
        categoryRepository.getByName(name)
                .filter(existingCategory -> !existingCategory.getId().equals(categoryId))
                .ifPresent(existingCategory -> {
                    throw new CategoryAlreadyExistsException(name);
                });
    }

    @Override
    @Transactional
    public CategoryGetDTO updateCategory(Long categoryId, CategoryUpdateDTO dto) {
        Category cat= this.getCategory(categoryId);

        //validate category name
        String name = validateName(dto.getName());

        //validate if category name already exists (excluding current category being updated)
        validateCategoryNameUnique(dto.getName(), dto.getParentCategory().getId());

        cat.setName(name);

        // Validate parent category
        validateParentCategory(categoryId, dto, cat);

        return saveAndReturn(cat);
    }

    private void validateParentCategory(Long categoryId, CategoryUpdateDTO dto, Category cat) {
        if (dto.getParentCategory() == null || dto.getParentCategory().getId() == null) {
            cat.setParentCategory(null);
        } else {
            Long parentId = dto.getParentCategory().getId();

            // Prevent assigning the same category as its own parent
            if (categoryId.equals(parentId)) {
                throw new IllegalArgumentException("A category cannot be its own parent");
            }
            //Prevent recursivity
            Category parent = this.getCategory(parentId);

            for (Category anc = parent; anc != null; anc = anc.getParentCategory()) {
                if (categoryId.equals(anc.getId())) {
                    throw new IllegalArgumentException("Assigning that parent would create a cycle");
                }
            }

            cat.setParentCategory(parent);
        }
    }

    @Override
    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(()->new CategoryNotFoundException(categoryId.toString()));
    }
}
