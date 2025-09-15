package com.patomicroservicios.notification_service.consumer;

import com.patomicroservicios.notification_service.events.EmailEvent;
import com.patomicroservicios.notification_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Service
public class EmailConsumer {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = "notifications.email.user")
    public void userRegistered(EmailEvent dto) {
        emailService.send(dto.getEmail(), dto.getSubject(), dto.getBody());
    }

    @RabbitListener(queues = "notifications.email.stock")
    public void stockLow(EmailEvent dto) {
        emailService.send(dto.getEmail(), dto.getSubject(), dto.getBody());
    }

    @RabbitListener(queues = "notifications.email.order")
    public void orderStatusChanged(EmailEvent dto) {
        emailService.send(dto.getEmail(), dto.getSubject(), dto.getBody());
    }
}
