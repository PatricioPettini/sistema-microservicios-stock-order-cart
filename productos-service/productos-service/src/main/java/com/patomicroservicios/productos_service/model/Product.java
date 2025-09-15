package com.patomicroservicios.productos_service.model;

import com.patomicroservicios.productos_service.exceptions.ProductAlreadyActiveException;
import com.patomicroservicios.productos_service.exceptions.ProductAlreadyInactiveException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @NotBlank(message = "name can't be blank")
    @Column(nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_brand"))
    private Brand brand;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;
    @NotNull(message = "unit price is required")
    @Column(nullable = false)
    private BigDecimal unitPrice;
    @NotNull(message = "status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Product.ProductState state;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ProductState {
        ACTIVE,
        INACTIVE
    }

    public void activate(){
        if(this.state == ProductState.ACTIVE) throw new ProductAlreadyActiveException(this.productId);
        this.state = ProductState.ACTIVE;
    }

    public void inactivate(){
        if(this.state == ProductState.INACTIVE) throw new ProductAlreadyInactiveException(this.productId);
        this.state = ProductState.INACTIVE;
    }

}
