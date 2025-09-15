package com.patomicroservicios.carrito_service.repository;

import com.patomicroservicios.carrito_service.model.Cart;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartRepository extends JpaRepository<Cart,Long> {

    @Query(value = "SELECT quantity FROM cart_product_list WHERE cart_id = :cartId AND product_id = :productId", nativeQuery = true)
    Integer getProductQuantity(@Param("cartId") Long cartId, @Param("productId") Long productId);

}
