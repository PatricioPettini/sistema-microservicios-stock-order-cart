package com.patomicroservicios.productos_service.service.implementations;

import com.patomicroservicios.productos_service.exceptions.ProductAlreadyExistsException;
import com.patomicroservicios.productos_service.exceptions.ProductNotFoundException;
import com.patomicroservicios.productos_service.dto.request.ProductCreateDTO;
import com.patomicroservicios.productos_service.dto.request.ProductPatchDTO;
import com.patomicroservicios.productos_service.dto.request.ProductUpdateDTO;
import com.patomicroservicios.productos_service.dto.response.ProductGetDTO;
import com.patomicroservicios.productos_service.model.Brand;
import com.patomicroservicios.productos_service.model.Category;
import com.patomicroservicios.productos_service.model.Product;
import com.patomicroservicios.productos_service.repository.IProductRepository;
import com.patomicroservicios.productos_service.service.interfaces.IBrandService;
import com.patomicroservicios.productos_service.service.interfaces.IProductService;
import com.patomicroservicios.productos_service.service.interfaces.ICategoryService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductService implements IProductService {

    @Autowired
    IProductRepository productRepository;

    @Autowired
    ICategoryService categoryService;

    @Autowired
    IBrandService brandService;

    @Autowired
    ModelMapper modelMapper;

    // register a new product
    @Override
    @Transactional
    public ProductGetDTO addProduct(ProductCreateDTO dto) {
        String name = dto.getName().trim();

        //validate if brand already exists by name
        validateNameAlreadyExists(dto, name);

        Product product= mapToProduct(dto, name);

        return saveAndReturn(product);
    }

    private Product mapToProduct(ProductCreateDTO dto, String name) {
        return Product.builder()
                .name(name)
                .brand(getBrand(dto.getBrandId()))
                .category(getCategory(dto.getCategoryId()))
                .unitPrice(dto.getUnitPrice())
                .state(Product.ProductState.ACTIVE)
                .build();
    }

    private Category getCategory(Long dto) {
        return categoryService.getCategory(dto);
    }

    private void validateNameAlreadyExists(ProductCreateDTO dto, String name) {
        productRepository.findByNameAndBrandId(name, dto.getBrandId())
                .ifPresent(p -> { throw new ProductAlreadyExistsException(name); });
    }

    private ProductGetDTO saveAndReturn(Product product) {
        Product savedProduct=productRepository.save(product);
        return toDto(savedProduct);
    }

    public Product getProduct(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(()->new ProductNotFoundException(productId));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product=getProduct(productId);
        productRepository.delete(product);
    }

    // update the entire product
    @Override
    @Transactional
    public ProductGetDTO updateProduct(ProductUpdateDTO dto, Long productId) {

        Product product=getProduct(productId);
        // map DTO values to product
        product.setState(dto.getState());
        product.setName(dto.getName());
        product.setCategory(getCategory(dto.getCategoryId()));
        product.setBrand(getBrand(dto.getBrandId()));

        validateUnitPrice(dto.getUnitPrice(), product);

        return saveAndReturn(product);
    }

    private Brand getBrand(Long dto) {
        return brandService.getBrand(dto);
    }

    public List<ProductGetDTO> getAll() {
        return productRepository.findAll().stream()
                .map(this::toDto)   // acá usamos ModelMapper
                .toList();
    }

    private ProductGetDTO toDto(Product product) {
        return modelMapper.map(product, ProductGetDTO.class);
    }

    @Override
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackProductNotFound")
    @Retry(name = "retryGetProductDTO")
    public ProductGetDTO getProductDTO(Long productId){
        return toDto(getProduct(productId));
    }

    // update some product fields
    @Override
    @Transactional
    public ProductGetDTO patchProduct(Long productId, ProductPatchDTO dto) {
        //get product
        Product product=getProduct(productId);

        // set field only if the DTO value is not null
        validateName(dto, product);

        validateUnitPrice(dto.getUnitPrice(), product);

        validateBrand(dto, product);

        validateCategory(dto, product);

        validateState(dto, product);

        return saveAndReturn(product);
    }

    private static void validateState(ProductPatchDTO dto, Product product) {
        Optional.ofNullable(dto.getState()).ifPresent(product::setState);
    }

    private void validateCategory(ProductPatchDTO dto, Product product) {
        Optional.ofNullable(dto.getCategoryId())
                .ifPresent(id -> product.setCategory(getCategory(id)));
    }

    private void validateBrand(ProductPatchDTO dto, Product product) {
        Optional.ofNullable(dto.getBrandId())
                .ifPresent(id -> product.setBrand(getBrand(id)));
    }

    private static void validateName(ProductPatchDTO dto, Product product) {
        Optional.ofNullable(dto.getName())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .ifPresent(product::setName);
    }

    private static void validateUnitPrice(BigDecimal price, Product product) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Price must be higher than 0");
        product.setUnitPrice(price);
    }

    @Override
    public ProductGetDTO changeProductState(Long productId, Product.ProductState state) {
        Product product=getProduct(productId);
        switch (state){
            case ACTIVE -> product.activate();
            case INACTIVE -> product.inactivate();
        }
        return saveAndReturn(product);
    }

    @Override
    public List<ProductGetDTO> filter(Long brandId, Long categoryId, Product.ProductState state) {
        return productRepository.filter(brandId,categoryId,state.toString())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ProductGetDTO> orderByUnitPrice(Boolean ascending) {
        Comparator<ProductGetDTO> comparator = Comparator.comparing(ProductGetDTO::getUnitPrice);

        if ((Boolean.FALSE.equals(ascending))) {
            comparator = comparator.reversed();
        }

        return getAll().stream()
                .sorted(comparator)
                .toList();
    }

    @Override
    public List<ProductGetDTO> findProductsByIds(List<Long> idList) {
        return productRepository.findAllById(idList).stream()
                .map(this::toDto)
                .toList();
    }

    public ProductGetDTO fallbackProductNotFound(Long productId, Throwable throwable) {
        log.warn("Fallback triggered for product {}. Reason: {}", productId, throwable.toString());

        return ProductGetDTO.builder()
                .productId(productId)
                .name("Product not available")
                .unitPrice(BigDecimal.valueOf(0))
                .state(Product.ProductState.INACTIVE)
                .fallback(true)
                .build();
    }

}
