package com.patomicroservicios.productos_service.repository;

import com.patomicroservicios.productos_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> getByName(String name);
}
