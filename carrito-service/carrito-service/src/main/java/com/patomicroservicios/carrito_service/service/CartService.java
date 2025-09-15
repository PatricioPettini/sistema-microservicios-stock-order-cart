package com.patomicroservicios.carrito_service.service;

import com.patomicroservicios.carrito_service.exceptions.*;
import com.patomicroservicios.carrito_service.dto.ProductQuantityDTO;
import com.patomicroservicios.carrito_service.dto.response.CartGetDTO;
import com.patomicroservicios.carrito_service.dto.response.ProductGetDTO;
import com.patomicroservicios.carrito_service.model.Cart;
import com.patomicroservicios.carrito_service.model.ProductQuantity;
import com.patomicroservicios.carrito_service.repository.ICartRepository;
import com.patomicroservicios.carrito_service.repository.ProductAPI;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartService implements ICartService {

    @Autowired
    ICartRepository cartRepository;

    @Autowired
    ProductAPI productAPI;

    @Autowired
    ModelMapper modelMapper;

    //add product to cart
    @Override
    @Transactional
    public CartGetDTO addProductToCart(Long cartId, Long productId, int quantity) {
        Cart cart = getCartEntity(cartId);

        boolean productExists=cart.getProductList().stream()
                .anyMatch(p->p.getProductId().equals(productId));

        if(productExists) throw new ProductInCartException(productId);

        ProductGetDTO productDTO = getProductDTO(productId);

        validateState(productId, productDTO);

        validateQuantity(quantity);

        ProductQuantity productQuantity=new ProductQuantity(productId, quantity);

        cart.getProductList().add(productQuantity);

        return saveAndReturn(cart);
    }

    private static void validateState(Long productId, ProductGetDTO productDTO) {
        Optional.of(productDTO)
                .filter(p -> "ACTIVE".equalsIgnoreCase(p.getState()))
                .orElseThrow(() -> new InactiveProductException(productId));
    }

    private ProductGetDTO getProductDTO(Long productId) {
        ProductGetDTO productGetDTO=productAPI.getProduct(productId);
        if(productGetDTO.isFallback()) throw new ProductNotFoundException(productId);
        return productGetDTO;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be higher than 0");
        }
    }

    //remove product from cart
    @Override
    @Transactional
    public void deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = getCartEntity(cartId);

        getProductDTO(productId);

        ProductQuantity pq=getProductQuantity(cartId,productId,cart);

        cart.getProductList()
                .removeIf(p -> p.getProductId().equals(productId));

        cartRepository.save(cart);
    }

    private ProductQuantityDTO mapToPQDTO(ProductQuantity productQuantity){
        return new ProductQuantityDTO(productQuantity.getProductId(),productQuantity.getQuantity());
    }

    // get cart with products
    @Override
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackCartNotFound")
    @Retry(name = "retryGetCart")
    public CartGetDTO getCart(Long cartId) {
        return toDto(getCartEntity(cartId));
    }

    public CartGetDTO fallbackCartNotFound(Long cartId, Throwable throwable) {
        log.warn("⚠️ Fallback triggered for cart {}. Reason: {}", cartId, throwable.toString());

        return CartGetDTO.builder()
                .id(cartId)
                .userId("999")
                .subtotalPrice(BigDecimal.valueOf(999))
                .productList(null)
                .fallback(true)
                .build();
    }


    private ProductGetDTO getProductFromAPI(ProductQuantity item) {
        ProductGetDTO product = getProductDTO(item.getProductId());

        product.setQuantity(item.getQuantity());
        return product;
    }

    public BigDecimal getUnitPrice(Long id){
        ProductGetDTO p = getProductDTO(id);

        return p.getUnitPrice();
    }

    public BigDecimal getTotalPrice(List<ProductQuantity> productQuantityList) {
        return productQuantityList.stream()
                .map(p -> {
                    BigDecimal unitPrice = getUnitPrice(p.getProductId());
                    return unitPrice.multiply(BigDecimal.valueOf(p.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<CartGetDTO> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(cart -> {
                    BigDecimal total = getTotalPrice(cart.getProductList());
                    CartGetDTO dto = toDto(cart);
                    dto.setSubtotalPrice(total);
                    return dto;
                })
                .toList();
    }

    //create new cart
    @Override
    public void addCart(String userId) {
        Cart cart = Cart.builder().userId(userId).build();

        cartRepository.save(cart);
    }

    @Override
    public void emptyCart(Long cartId) {
        Cart cart = getCartEntity(cartId);

        cart.setProductList(new ArrayList<>());
        cartRepository.save(cart);
    }

    private Cart getCartEntity(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(()->new CartNotFoundException(cartId));
    }

    @Override
    @Transactional
    public CartGetDTO editProductQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = getCartEntity(cartId);

        ProductGetDTO productDTO = getProductDTO(productId);

        validateState(productId, productDTO);

        validateQuantity(quantity);

        ProductQuantity pq = getProductQuantity(cartId, productId, cart);

        pq.setQuantity(quantity);

        return saveAndReturn(cart);
    }

    private CartGetDTO saveAndReturn(Cart cart) {
        Cart saved= cartRepository.save(cart);
        CartGetDTO dto = toDto(saved);
        dto.setSubtotalPrice(getTotalPrice(saved.getProductList()));
        return dto;
    }

    private static ProductQuantity getProductQuantity(Long cartId, Long productId, Cart cart) {
        return cart.getProductList()
                .stream()
                .filter(p->p.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(()->new ProductNotInCartException(cartId, productId));
    }

    private CartGetDTO toDto(Cart cart) {
        Map<Long, Integer> quantityMap = cart.getProductList().stream()
                .collect(Collectors.toMap(
                        ProductQuantity::getProductId,
                        ProductQuantity::getQuantity
                ));

        List<ProductGetDTO> products = productAPI.getProductsByIds(
                        new ArrayList<>(quantityMap.keySet()) // 👈 conversión Set -> List
                ).stream()
                .peek(p -> p.setQuantity(quantityMap.getOrDefault(p.getProductId(), 0)))
                .toList();


        return CartGetDTO.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .productList(products)
                .subtotalPrice(getTotalPrice(cart.getProductList()))
                .build();
    }

    @Override
    public boolean isOwner(Long cartId, String userId) {
        CartGetDTO cart = getCart(cartId);
        return cart.getUserId().equals(userId);
    }


}