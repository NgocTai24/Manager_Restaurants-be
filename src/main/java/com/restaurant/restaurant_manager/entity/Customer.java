package com.restaurant.restaurant_manager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone; // Đây là "khóa chính" nghiệp vụ

    @Column(unique = true)
    private String email;

    // --- CÁC TRƯỜNG BỔ SUNG ĐỂ QUẢN LÝ ---

    @Column(columnDefinition = "TEXT")
    private String address; // Địa chỉ (cho các đơn hàng giao đi)

    private LocalDate dateOfBirth; // Ngày sinh (để gửi ưu đãi)

    @Column(columnDefinition = "TEXT")
    private String notes; // Ghi chú (ví dụ: "Khách VIP", "Dị ứng hải sản")

    private int loyaltyPoints; // Điểm tích lũy (nếu có)

    // --- CÁC QUAN HỆ ---

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reservation> reservations;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders;
}