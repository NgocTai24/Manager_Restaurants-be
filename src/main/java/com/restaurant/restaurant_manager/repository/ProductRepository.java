// src/main/java/com/restaurant/restaurant_manager/repository/ProductRepository.java
package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> { }