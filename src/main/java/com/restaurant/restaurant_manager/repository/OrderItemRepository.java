// src/main/java/com/restaurant/restaurant_manager/repository/OrderItemRepository.java
package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> { }