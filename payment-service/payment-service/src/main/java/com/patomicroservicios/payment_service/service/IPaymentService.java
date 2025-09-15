package com.patomicroservicios.payment_service.service;

import com.patomicroservicios.payment_service.dto.request.PaymentCreateDTO;
import com.patomicroservicios.payment_service.dto.response.PaymentDTO;
import com.patomicroservicios.payment_service.model.Payment;

import java.util.Optional;

public interface IPaymentService{
    PaymentDTO newPayment(PaymentCreateDTO payment);
    PaymentDTO getPaymentByOrderId(Long orderId);
    PaymentDTO cancelPayment(Long orderId);
    boolean isClient(Long orderId, String userId);
}
