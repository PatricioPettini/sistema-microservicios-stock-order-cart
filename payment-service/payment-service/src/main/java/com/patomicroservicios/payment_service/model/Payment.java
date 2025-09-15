package com.patomicroservicios.payment_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private Long orderId;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PAYMENT_METHOD method;
    @Enumerated(EnumType.STRING)
    private PAYMENT_STATUS status;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum PAYMENT_STATUS {COMPLETED, CANCELED}

    public enum PAYMENT_METHOD {CREDIT_CARD, PAYPAL, CASH}
}
