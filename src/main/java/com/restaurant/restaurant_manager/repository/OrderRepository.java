package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Tìm tất cả order của một khách hàng
    List<Order> findByCustomerId(UUID customerId);

    // Query tối ưu để fetch cả items tránh N+1 Query
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.id = :id")
    Optional<Order> findByIdWithItems(UUID id);
}