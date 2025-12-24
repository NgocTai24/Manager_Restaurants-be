package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<News, UUID> {
    // Lấy tin tức mới nhất trước
    List<News> findAllByOrderByCreatedAtDesc();

    boolean existsByTitle(String title);
}