package com.patomicroservicios.productos_service.repository;

import com.patomicroservicios.productos_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByNameAndBrandId(String name, Long brandId);

    @Query("""
    SELECT p FROM Product p
    WHERE (:brandId IS NULL OR p.brand.id = :brandId)
      AND (:categoryId IS NULL OR p.category.id = :categoryId)
      AND (:state IS NULL OR p.state = :state)
""")
    List<Product> filter(Long brandId, Long categoryId, String state);

}
