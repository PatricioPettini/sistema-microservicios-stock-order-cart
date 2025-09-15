package com.patomicroservicios.carrito_service.consumer;

import com.patomicroservicios.carrito_service.config.RabbitMQConfig;
import com.patomicroservicios.carrito_service.events.UserRegisteredEvent;
import com.patomicroservicios.carrito_service.model.Cart;
import com.patomicroservicios.carrito_service.service.CartService;
import com.patomicroservicios.carrito_service.service.ICartService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class CartConsumer {

    private final ICartService cartService;

    public CartConsumer(CartService cartService) {
        this.cartService = cartService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleUserRegistered(UserRegisteredEvent event) {
        System.out.println("📥 Evento recibido en Cart Service: " + event);

        if (event == null || event.getUserId() == null) {
            System.err.println("⚠️ Evento inválido recibido en Cart Service");
            return; // cortás acá, no intentás guardar en DB
        }
        cartService.addCart(event.getUserId());
    }

}
