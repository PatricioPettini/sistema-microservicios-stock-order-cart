package com.patomicroservicios.user_service.producer;

import com.patomicroservicios.user_service.config.RabbitMQConfig;
import com.patomicroservicios.user_service.events.UserRegisteredEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;

    public UserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserRegistered(String userId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                new UserRegisteredEvent(userId)
        );
        System.out.println("📤 Evento de usuario registrado enviado: " + userId);
    }
}
