package com.restaurant.restaurant_manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.restaurant_manager.entity.enums.PaymentMethod;
import com.restaurant.restaurant_manager.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @JsonIgnore
    private Order order;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // PayOS Transaction Info
    private Long payosOrderCode;
    private String payosTransactionId;

    @Column(columnDefinition = "TEXT")
    private String paymentUrl;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    @Column(columnDefinition = "TEXT")
    private String note;
}