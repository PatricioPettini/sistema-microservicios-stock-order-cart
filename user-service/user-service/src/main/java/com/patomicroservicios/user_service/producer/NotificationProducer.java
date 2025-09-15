package com.patomicroservicios.user_service.producer;

import com.patomicroservicios.user_service.dto.EmailEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserRegisteredEmail(String email) {
        String subject = "Welcome to our site";
        String body = "Thanks for registering, " + email;

        rabbitTemplate.convertAndSend(
                "notifications.exchange",
                "user.registered",
                new EmailEvent(email, subject, body)
        );
    }
}
