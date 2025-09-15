package com.patomicroservicios.order_service.service;
import com.patomicroservicios.order_service.dto.response.OrderGetDTO;

import java.util.List;

public interface IOrderService {
    OrderGetDTO createOrder(Long cartId);
    OrderGetDTO cancelOrder(Long orderId);
    OrderGetDTO getOrder(Long orderId);
    boolean isClient(Long orderId, String userId);
    boolean isCartOwner(Long cartId, String userId);


}
