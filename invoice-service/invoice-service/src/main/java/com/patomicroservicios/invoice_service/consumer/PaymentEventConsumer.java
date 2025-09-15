package com.patomicroservicios.invoice_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patomicroservicios.invoice_service.config.RabbitMQConfig;
import com.patomicroservicios.invoice_service.event.PaymentCompletedEvent;
import com.patomicroservicios.invoice_service.service.IInvoiceService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    private final IInvoiceService invoiceService;

    public PaymentEventConsumer(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_COMPLETED_QUEUE)
    public void handlePaymentCompleted(String json) throws JsonProcessingException {
        System.out.println("📩 Evento recibido (raw JSON): " + json);
        PaymentCompletedEvent event = new ObjectMapper().readValue(json, PaymentCompletedEvent.class);
        invoiceService.createInvoice(event.getOrderId());
    }

}
