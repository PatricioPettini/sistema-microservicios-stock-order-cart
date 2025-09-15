package com.patomicroservicios.productos_service.controller;

import com.patomicroservicios.productos_service.dto.request.ProductCreateDTO;
import com.patomicroservicios.productos_service.dto.request.ProductFilterDTO;
import com.patomicroservicios.productos_service.dto.request.ProductPatchDTO;
import com.patomicroservicios.productos_service.dto.request.ProductUpdateDTO;
import com.patomicroservicios.productos_service.dto.response.ProductGetDTO;
import com.patomicroservicios.productos_service.model.Product;
import com.patomicroservicios.productos_service.service.interfaces.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    IProductService productService;

    @Value("${server.port}")
    private int serverPort;

    @Operation(
            summary = "Get product by ID",
            description = "Returns a single product identified by its unique ID."
    )
    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('SYSTEM')")
    public ResponseEntity<ProductGetDTO> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductDTO(productId));
    }

    @Operation(
            summary = "Get multiple products by IDs",
            description = "Returns all products that match the provided list of IDs (comma-separated)."
    )
    @GetMapping("/by-ids")
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<List<ProductGetDTO>> getProductsByIds(@RequestParam("ids") List<Long> productIds) {
        return ResponseEntity.ok(productService.findProductsByIds(productIds));
    }

    @Operation(
            summary = "Filter products",
            description = "Returns products matching the given criteria (brand, category, and state)."
    )
    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ProductGetDTO>> getFilteredProducts(@RequestBody ProductFilterDTO dto) {
        return ResponseEntity.ok(productService.filter(dto.getBrandId(), dto.getCategoryId(), dto.getState()));
    }

    @Operation(
            summary = "Order products by price",
            description = "Returns products ordered by unit price. Use true for ascending and false for descending."
    )
    @GetMapping("/order/price/{ascending}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ProductGetDTO>> getProductsOrderedByPrice(@PathVariable Boolean ascending) {
        return ResponseEntity.ok(productService.orderByUnitPrice(ascending));
    }

    @Operation(
            summary = "Partially update product",
            description = "Updates specific fields of a product and returns the updated resource."
    )
    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductGetDTO> patchProduct(@PathVariable Long productId,
                                                      @Valid @RequestBody ProductPatchDTO product) {
        return ResponseEntity.ok(productService.patchProduct(productId, product));
    }

    @Operation(
            summary = "Change product state",
            description = "Activates or deactivates a product by setting its state."
    )
    @PatchMapping("/{id}/state")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductGetDTO> changeState(@PathVariable Long id,
                                                     @RequestParam Product.ProductState state) {
        ProductGetDTO updated = productService.changeProductState(id, state);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "List all products",
            description = "Returns the complete list of products."
    )
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ProductGetDTO>> getAllProducts() {
        System.out.println("server: " + serverPort); // Load Balancer demo
        return ResponseEntity.ok(productService.getAll());
    }

    @Operation(
            summary = "Create product",
            description = "Creates a new product and returns the created resource."
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductGetDTO> addProduct(@Valid @RequestBody ProductCreateDTO product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(product));
    }

    @Operation(
            summary = "Update product",
            description = "Replaces all fields of an existing product and returns the updated resource."
    )
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductGetDTO> putProduct(@PathVariable Long productId,
                                                    @Valid @RequestBody ProductUpdateDTO product) {
        return ResponseEntity.ok(productService.updateProduct(product, productId));
    }

    @Operation(
            summary = "Delete product",
            description = "Deletes a product by its ID."
    )
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build(); // 204
    }
}
