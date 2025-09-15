package com.patomicroservicios.order_service.service;

import com.patomicroservicios.order_service.dto.response.CartDTO;
import com.patomicroservicios.order_service.dto.request.ProductQuantityDTO;
import com.patomicroservicios.order_service.dto.response.OrderGetDTO;
import com.patomicroservicios.order_service.dto.response.ProductDTO;
import com.patomicroservicios.order_service.exceptions.CartNotFoundException;
import com.patomicroservicios.order_service.exceptions.OrderAlreadyCanceled;
import com.patomicroservicios.order_service.exceptions.OrderNotFoundException;
import com.patomicroservicios.order_service.model.Order;
import com.patomicroservicios.order_service.model.Product;
import com.patomicroservicios.order_service.producer.NotificationProducer;
import com.patomicroservicios.order_service.repository.CartAPI;
import com.patomicroservicios.order_service.repository.IOrderRepository;
import com.patomicroservicios.order_service.repository.StockAPI;
import com.patomicroservicios.order_service.repository.UserAPI;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Service
public class OrderService implements IOrderService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    IOrderRepository orderRepository;

    @Autowired
    CartAPI cartAPI;

    @Autowired
    StockAPI stockAPI;

    @Autowired
    UserAPI userAPI;

    @Autowired
    NotificationProducer notificationProducer;

    @Override
    @Transactional
    public OrderGetDTO createOrder(Long cartId) {
        CartDTO cartDTO = getCartDTO(cartId);
        BigDecimal subtotal = cartDTO.getSubtotalPrice();
        BigDecimal taxes = subtotal.multiply(BigDecimal.valueOf(0.21));

        Order order= mapToOrder(cartDTO, subtotal, taxes);
        order.setStatus(Order.OrderStatus.CREATED);

        List<ProductQuantityDTO> dtoList=order.getItems()
                .stream()
                .map(mapToProductQuantity()
                ).toList();

        dtoList.forEach(pq->stockAPI.reserveStock(pq));

        Order saved=orderRepository.save(order);

        notifyOrder(order.getEmail(), order.getId(),order.getStatus().toString());

        return toDto(saved);
    }

    private CartDTO getCartDTO(Long cartId) {
        CartDTO cartDTO=cartAPI.getCart(cartId);
        if(cartDTO.isFallback()) throw new CartNotFoundException(cartId);
        return cartDTO;
    }

    private Order mapToOrder(CartDTO cartDTO, BigDecimal subtotal, BigDecimal taxes) {
        return Order.builder()
                .userId(cartDTO.getUserId())
                .email(userAPI.getEmailByUserId(cartDTO.getUserId()))
                .code(codeGenerator())
                .items(
                        getProductList(cartDTO)
                )
                .subtotalPrice(subtotal)
                .taxes(taxes)
                .totalPrice(subtotal.add(taxes))
                .build();
    }

    private static List<Product> getProductList(CartDTO cartDTO) {
        return cartDTO.getProductList().stream()
                .map(OrderService::mapToProduct)
                .toList();
    }

    private static Product mapToProduct(ProductDTO p) {
        var unitPrice = p.getUnitPrice();
        var quantity = p.getQuantity();
        return Product.builder()
                .productId(p.getProductId())
                .name(p.getName())
                .unitPrice(unitPrice)
                .quantity(quantity)
                .subtotalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                .build();
    }

    public static String codeGenerator() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100, 1000);
        return "PED-" + timestamp + "-" + random;
    }

    @Override
    @Transactional
    public OrderGetDTO cancelOrder(Long orderId) {
        Order order = getOrderEntity(orderId);

        //validate state
        if(order.getStatus().equals(Order.OrderStatus.CANCELED)) throw new OrderAlreadyCanceled(orderId);

        updateOrderStatus(orderId, Order.OrderStatus.CANCELED);

        //restore stock
        List<ProductQuantityDTO> dtoList=order.getItems()
                .stream()
                .map(mapToProductQuantity()
                ).toList();

        stockAPI.restoreReservedStock(dtoList);

        return toDto(order);
    }

    private static Function<Product, ProductQuantityDTO> mapToProductQuantity() {
        return product ->
                ProductQuantityDTO
                        .builder()
                        .productId(product.getProductId())
                        .quantity(product.getQuantity())
                        .build();
    }

    public void notifyOrder(String destination, Long orderId, String status) {
        notificationProducer.sendOrderStatusChanged(destination, orderId, status);
    }

    private Order getOrderEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(()->new OrderNotFoundException(orderId));
    }

    @Override
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackOrderNotFound")
    @Retry(name = "retryGetOrder")
    public OrderGetDTO getOrder(Long orderId) {
        Order order = getOrderEntity(orderId);

        return toDto(order);
    }

    @Override
    public boolean isClient(Long orderId, String userId) {
        return getOrder(orderId).getUserId().equals(userId);
    }

    @Override
    public boolean isCartOwner(Long cartId, String userId) {
        return cartAPI.getCart(cartId).getUserId().equals(userId);
    }

    public OrderGetDTO fallbackOrderNotFound(Long orderId, Throwable throwable) {

        return OrderGetDTO.builder()
                .id(orderId)
                .code("999")
                .userId("999")
                .status("FAILED")
                .fallback(true)
                .build();
    }

    private OrderGetDTO toDto(Order order){
        return modelMapper.map(order,OrderGetDTO.class);
    }

    public void updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = getOrderEntity(orderId);

        if (order.getStatus().equals(newStatus)) {
            throw new IllegalStateException("Order " + orderId + " is already " + newStatus);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        notifyOrder(order.getEmail(),orderId,newStatus.toString());
        System.out.println("✅ Orden " + orderId + " actualizada a " + newStatus);
    }

}
