package com.patomicroservicios.carrito_service.service;

import com.patomicroservicios.carrito_service.dto.response.CartGetDTO;

import java.util.List;

public interface ICartService {
    CartGetDTO addProductToCart(Long cartId, Long productId, int quantity);
    void deleteProductFromCart(Long cartId, Long productId);
    CartGetDTO getCart(Long cartId);
    List<CartGetDTO> getAllCarts();
    void addCart(String userId);
    void emptyCart(Long cartId);
    CartGetDTO editProductQuantity(Long cartId, Long productId, int quantity);
    boolean isOwner(Long cartId, String userId);
}
