package com.patomicroservicios.order_service.consumer;

import com.patomicroservicios.order_service.config.RabbitMQConfig;
import com.patomicroservicios.order_service.event.PaymentCompletedEvent;
import com.patomicroservicios.order_service.model.Order;
import com.patomicroservicios.order_service.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    private final OrderService orderService;

    public PaymentEventConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_COMPLETED_QUEUE)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        System.out.println("📥 Evento recibido: pago completado para orderId=" + event.getPaymentId());
        orderService.updateOrderStatus(event.getOrderId(), Order.OrderStatus.PAID);
    }
}
