package com.patomicroservicios.productos_service.repository;

import com.patomicroservicios.productos_service.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IBrandRepository extends JpaRepository<Brand,Long> {
    Optional<Brand> getByName(String name);
}
