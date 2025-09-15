package com.patomicroservicios.payment_service.repository;

import com.patomicroservicios.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findPaymentByOrderId(Long orderId);

}
