package com.patomicroservicios.invoice_service.service;

import com.patomicroservicios.invoice_service.dto.InvoiceGetDTO;

public interface IInvoiceService {
    void createInvoice(Long orderId);
    InvoiceGetDTO getInvoiceByOrderId(Long orderId);
    byte[] generateInvoicePdf(Long invoiceId);
    boolean isOwnerOfOrder(Long orderId, String userId);
    Long getOrderByInvoiceId(Long invoiceId);
}
