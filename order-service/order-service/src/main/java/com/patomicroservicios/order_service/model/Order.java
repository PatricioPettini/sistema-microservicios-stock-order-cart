package com.patomicroservicios.order_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @NotNull(message = "userId cant be empty")
    @Column(nullable = false)
    private String userId;
    @NotNull(message = "user email cant be empty")
    @Column(nullable = false)
    private String email;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;
    @ElementCollection
    @CollectionTable(name = "pedido_item", joinColumns = @JoinColumn(name = "pedido_id"))
    @Builder.Default
    private List<Product> items = new ArrayList<>();
    @Builder.Default
    private BigDecimal subtotalPrice = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal taxes = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum OrderStatus {
        CREATED,
        PAID,
        CANCELED
    }
}


