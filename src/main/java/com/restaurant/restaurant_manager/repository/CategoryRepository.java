package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // 1. Kiểm tra tồn tại (Dùng cho CategorySeeder)
    boolean existsByName(String name);

    // 2. Tìm theo tên (Dùng cho ProductSeeder để lấy Category gán cho Product)
    Optional<Category> findByName(String name);
}