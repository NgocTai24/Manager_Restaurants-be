package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * ✅ Load Order cùng với OrderItems và Product trong 1 query
     * Tránh N+1 query problem
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product " +
            "WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") UUID id);
}