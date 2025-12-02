package com.restaurant.restaurant_manager.entity;

import com.restaurant.restaurant_manager.entity.enums.TableStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTable {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name; // Ví dụ: Bàn 01, VIP 02

    @Column(nullable = false)
    private int capacity; // Số ghế: 2, 4, 6, 10

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status; // AVAILABLE, OCCUPIED, RESERVED (Trạng thái hiện tại)

    private String description; // Ví dụ: "Gần cửa sổ", "Phòng lạnh"

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    private Set<Reservation> reservations = new HashSet<>();
}