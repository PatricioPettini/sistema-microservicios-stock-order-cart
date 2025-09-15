package com.patomicroservicios.invoice_service.repository;

import com.patomicroservicios.invoice_service.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice,Long> {

    Optional<Invoice> findInvoiceByOrderId(Long orderId);

}
