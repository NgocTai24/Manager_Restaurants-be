// src/main/java/com/restaurant/restaurant_manager/repository/OrderRepository.java
package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> { }