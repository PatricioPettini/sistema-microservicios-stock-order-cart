package com.patomicroservicios.order_service.producer;

import com.patomicroservicios.order_service.dto.EmailEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderStatusChanged(String email, Long orderId, String status) {
        String subject = "Your order #" + orderId + " changed the status";
        String body = "Actual status: " + status.toUpperCase();

        rabbitTemplate.convertAndSend(
                "notifications.exchange",
                "order.status.changed",
                new EmailEventDTO(email, subject, body)
        );
    }
}
