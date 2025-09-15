package com.patomicroservicios.payment_service.service;

import com.patomicroservicios.payment_service.config.RabbitMQConfig;
import com.patomicroservicios.payment_service.dto.request.ProductQuantityDTO;
import com.patomicroservicios.payment_service.event.PaymentCompletedEvent;
import com.patomicroservicios.payment_service.dto.request.PaymentCreateDTO;
import com.patomicroservicios.payment_service.dto.response.OrderGetDTO;
import com.patomicroservicios.payment_service.dto.response.PaymentDTO;
import com.patomicroservicios.payment_service.exceptions.OrderNotFoundException;
import com.patomicroservicios.payment_service.exceptions.PaymentAlreadyCompletedException;
import com.patomicroservicios.payment_service.exceptions.PaymentAlreadyExistsException;
import com.patomicroservicios.payment_service.exceptions.PaymentNotFoundException;
import com.patomicroservicios.payment_service.model.Payment;
import com.patomicroservicios.payment_service.repository.IPaymentRepository;
import com.patomicroservicios.payment_service.repository.OrderAPI;
import com.patomicroservicios.payment_service.repository.StockAPI;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService implements IPaymentService{

    @Autowired
    IPaymentRepository paymentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    OrderAPI orderAPI;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    StockAPI stockAPI;

    @Override
    public PaymentDTO newPayment(PaymentCreateDTO paymentCreateDTO) {
        Long orderId=paymentCreateDTO.getOrderId();
        OrderGetDTO orderDTO=getOrderDTO(orderId);

        if (paymentRepository.findPaymentByOrderId(orderId).isPresent()) {
            throw new PaymentAlreadyExistsException(orderId);
        }

        if (orderDTO.getStatus().equals("PAID") || orderDTO.getStatus().equals("CANCELED")) {
            throw new IllegalStateException("Order is already paid or canceled.");
        }

        Payment payment=toModel(paymentCreateDTO);
        payment.setAmount(orderDTO.getTotalPrice());
        payment.setUserId(orderDTO.getUserId());
        payment.setStatus(Payment.PAYMENT_STATUS.COMPLETED);
        Payment saved = paymentRepository.save(payment);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.PAYMENT_COMPLETED_ROUTING_KEY,
                new PaymentCompletedEvent(saved.getOrderId(), saved.getId(), saved.getAmount())
        );

        List<ProductQuantityDTO> dtoList= orderDTO.getItems().stream()
                                            .map(pq-> ProductQuantityDTO.builder()
                                                    .productId(pq.getProductId())
                                                    .quantity(pq.getQuantity())
                                                    .build())
                                            .toList();

        stockAPI.subtractStock(dtoList);

        return toDTO(saved);
    }

    private PaymentDTO saveAndReturn(Payment payment) {
        Payment saved=paymentRepository.save(payment);
        return toDTO(saved);
    }

    @Override
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackPaymentNotFound")
    @Retry(name = "retryGetPaymentByOrderId")
    public PaymentDTO getPaymentByOrderId(Long orderId) {
        return toDTO(paymentRepository.findPaymentByOrderId(orderId)
                .orElseThrow(PaymentNotFoundException::new));
    }

    public PaymentDTO fallbackPaymentNotFound(Long paymentId, Throwable throwable) {
        return PaymentDTO.builder()
                .userId("999")
                .orderId(paymentId)
                .amount(999.9)
                .method("FAILED")
                .status("FAILED")
                .fallback(true)
                .build();
    }

    @Override
    public PaymentDTO cancelPayment(Long orderId) {
        Payment payment=paymentRepository.findPaymentByOrderId(orderId)
                .orElseThrow(PaymentNotFoundException::new);

        if (payment.getStatus() == Payment.PAYMENT_STATUS.COMPLETED) {
            throw new PaymentAlreadyCompletedException(orderId);
        }
        if (payment.getStatus() == Payment.PAYMENT_STATUS.CANCELED) {
            return toDTO(payment);
        }
        payment.setStatus(Payment.PAYMENT_STATUS.CANCELED);
        return saveAndReturn(payment);
    }

    @Override
    public boolean isClient(Long orderId, String userId) {
        return orderAPI.getOrder(orderId).getUserId().equals(userId);
    }

    private Payment toModel(PaymentCreateDTO paymentCreateDTO) {
        Payment.PAYMENT_METHOD method;
        try {
            method = Payment.PAYMENT_METHOD.valueOf(paymentCreateDTO.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Method Payment: " + paymentCreateDTO.getMethod());
        }

        return Payment.builder()
                .orderId(paymentCreateDTO.getOrderId())
                .method(method)
                .build();
    }

    private PaymentDTO toDTO(Payment payment){
        PaymentDTO paymentDTO= modelMapper.map(payment,PaymentDTO.class);
        paymentDTO.setStatus(payment.getStatus().toString());
        paymentDTO.setMethod(payment.getMethod().toString());
        return paymentDTO;
    }

    private OrderGetDTO getOrderDTO(Long orderId){
        OrderGetDTO orderGetDTO=orderAPI.getOrder(orderId);
        if (orderGetDTO.isFallback()) throw new OrderNotFoundException(orderId);
        return orderGetDTO;
    }
}
