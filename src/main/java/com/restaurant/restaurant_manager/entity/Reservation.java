package com.restaurant.restaurant_manager.entity;

import com.restaurant.restaurant_manager.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    // Thời gian khách dự kiến đến
    @Column(nullable = false)
    private LocalDateTime reservationTime;

    // Thời gian dự kiến trả bàn (Thường mặc định là reservationTime + 2 tiếng)
    // Trường này quan trọng để check bàn trống
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private int numberOfGuests;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Thông tin khách hàng (Liên kết với bảng Customer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Bàn được gán (Có thể null lúc mới đặt, Staff sẽ xếp bàn sau hoặc hệ thống tự xếp)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = true)
    private RestaurantTable table;
}