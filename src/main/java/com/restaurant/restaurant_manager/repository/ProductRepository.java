package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByName(String name);

    Page<Product> findByCategoryId(UUID categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN OrderItem oi ON p.id = oi.product.id " +
            "JOIN Order o ON oi.order.id = o.id " +
            "WHERE o.status = 'COMPLETED' " +
            "GROUP BY p.id " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Product> findTopSellingProducts(Pageable pageable);
}