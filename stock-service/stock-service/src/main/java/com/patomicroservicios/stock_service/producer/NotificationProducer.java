package com.patomicroservicios.stock_service.producer;

import com.patomicroservicios.stock_service.dto.EmailEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${ADMIN_EMAIL}")
    private String stockAlertEmail;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendLowStockAlert(Long productId, int quantity) {
        String subject = "⚠️ Low stock";
        String body = "Product with Id  '" + productId + "' has " + quantity + " units of stock.";

        rabbitTemplate.convertAndSend(
                "notifications.exchange",
                "stock.low",
                new EmailEventDTO(stockAlertEmail, subject, body)
        );
    }
}
