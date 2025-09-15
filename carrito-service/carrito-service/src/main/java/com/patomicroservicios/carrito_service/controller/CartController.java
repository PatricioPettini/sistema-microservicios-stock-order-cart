package com.patomicroservicios.carrito_service.controller;

import com.patomicroservicios.carrito_service.dto.request.CartProductsDTO;
import com.patomicroservicios.carrito_service.dto.response.CartGetDTO;
import com.patomicroservicios.carrito_service.dto.request.CartProductDeleteDTO;
import com.patomicroservicios.carrito_service.service.ICartService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    ICartService cartService;

    @Operation(
            summary = "Get Cart by Id",
            description = "Retrieve a shopping cart based on its unique identifier."
    )
    @GetMapping("/{cartId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('SYSTEM')")
    public ResponseEntity<CartGetDTO> getCart(
            @PathVariable Long cartId,
            Authentication authentication) {

        // El JWT completo
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // El "sub" del token (puede ser userId si es un usuario humano, o client_id si es un microservicio)
        String userIdOrClient = jwt.getSubject();

        // Verificamos si el rol es ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Verificamos si es SYSTEM (llamadas internas de otros microservicios)
        boolean isSystem = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SYSTEM"));

        CartGetDTO cartDTO = cartService.getCart(cartId);

        // Si no es admin ni system, solo el dueño puede acceder
        if (!isAdmin && !isSystem && !cartService.isOwner(cartId, userIdOrClient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(cartDTO);
    }

    @Operation(
            summary = "Get All Carts",
            description = "Retrieve a list of all shopping carts."
    )
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CartGetDTO> getAllCarts() {
        return cartService.getAllCarts();
    }

    @Operation(
            summary = "Add Product to Cart",
            description = "Add a product with a specific quantity to a shopping cart."
    )
    @PostMapping("/product")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartGetDTO> addProductToCart(@RequestBody CartProductsDTO dto,
                                                       @AuthenticationPrincipal Jwt jwt) {
        // Solo dueño del cart
        if (!cartService.isOwner(dto.getCartId(), jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        CartGetDTO cartDTO = cartService.addProductToCart(dto.getCartId(), dto.getProductId(), dto.getQuantity());

        return ResponseEntity.ok(cartDTO);
    }

    @Operation(
            summary = "Update Product Quantity in Cart",
            description = "Update the quantity of a specific product inside a shopping cart."
    )
    @PatchMapping("/product")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartGetDTO> updateProductQuantityInCart(@RequestBody CartProductsDTO dto,
                                                                  @AuthenticationPrincipal Jwt jwt) {
        // Solo dueño del cart
        if (!cartService.isOwner(dto.getCartId(), jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        CartGetDTO cartDTO = cartService.editProductQuantity(dto.getCartId(), dto.getProductId(), dto.getQuantity());
        return ResponseEntity.ok(cartDTO);
    }

    @Operation(
            summary = "Remove Product from Cart",
            description = "Remove a specific product from a shopping cart."
    )
    @DeleteMapping("/product")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> removeProductFromCart(@RequestBody CartProductDeleteDTO dto,
                                                        @AuthenticationPrincipal Jwt jwt) {
        // Solo dueño del cart
        if (!cartService.isOwner(dto.getCartId(), jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        cartService.deleteProductFromCart(dto.getCartId(), dto.getProductId());
        return ResponseEntity.ok("Product removed from cart successfully!");
    }

    @Operation(
            summary = "Empty Cart",
            description = "Remove all products from a shopping cart, leaving it empty."
    )
    @PutMapping("/empty/{cartId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> emptyCart(@PathVariable Long cartId,
                                            @AuthenticationPrincipal Jwt jwt) {
        if (!cartService.isOwner(cartId, jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        cartService.emptyCart(cartId);
        return ResponseEntity.ok("Cart emptied successfully!");
    }
}
