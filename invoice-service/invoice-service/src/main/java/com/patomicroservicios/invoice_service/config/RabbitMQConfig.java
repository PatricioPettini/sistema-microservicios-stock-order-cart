package com.patomicroservicios.invoice_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "notifications.exchange";
    public static final String PAYMENT_COMPLETED_QUEUE = "invoice.payment.completed.queue"; // 👈 NUEVA COLA SOLO PARA FACTURAS
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";

    @Bean
    public Queue invoicePaymentCompletedQueue() {
        return new Queue(PAYMENT_COMPLETED_QUEUE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding bindingInvoice(Queue invoicePaymentCompletedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(invoicePaymentCompletedQueue)
                .to(exchange)
                .with(PAYMENT_COMPLETED_ROUTING_KEY);
    }
}

