package com.patomicroservicios.stock_service.repository;

import com.patomicroservicios.stock_service.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IStockRepository extends JpaRepository<Stock,Long> {

    Optional<Stock> findByProductId(Long productId);
}
