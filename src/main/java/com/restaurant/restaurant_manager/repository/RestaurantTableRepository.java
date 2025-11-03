package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, UUID> {
    // Giao diện này giờ quản lý 'RestaurantTable'
}