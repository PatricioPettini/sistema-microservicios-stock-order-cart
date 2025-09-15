package com.patomicroservicios.productos_service.service.interfaces;

import com.patomicroservicios.productos_service.dto.request.ProductCreateDTO;
import com.patomicroservicios.productos_service.dto.request.ProductPatchDTO;
import com.patomicroservicios.productos_service.dto.request.ProductUpdateDTO;
import com.patomicroservicios.productos_service.dto.response.ProductGetDTO;
import com.patomicroservicios.productos_service.model.Product;

import java.util.List;

public interface IProductService {
    ProductGetDTO addProduct(ProductCreateDTO product);
    void deleteProduct(Long productId);
    ProductGetDTO updateProduct(ProductUpdateDTO product, Long productId);
    List<ProductGetDTO> getAll();
    ProductGetDTO getProductDTO(Long productId);
    ProductGetDTO patchProduct(Long productId, ProductPatchDTO product);
    ProductGetDTO changeProductState(Long productId, Product.ProductState state);
    List<ProductGetDTO> filter(Long BrandId, Long categoryId, Product.ProductState state);
    List<ProductGetDTO> orderByUnitPrice(Boolean ascending);
    List<ProductGetDTO> findProductsByIds(List<Long> idList);
}
