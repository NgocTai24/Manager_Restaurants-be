// src/main/java/com/restaurant/restaurant_manager/repository/ArticleRepository.java
package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> { }