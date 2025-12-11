package com.restaurant.restaurant_manager.dto.news;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.restaurant_manager.entity.News;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

public class NewsDTOs {

    @Data
    public static class CreateNewsRequest {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Content is required")
        private String content;

        private String imageUrl;
    }

    @Data
    public static class UpdateNewsRequest {
        private String title;
        private String content;
        private String imageUrl;
    }

    @Data
    @Builder
    public static class NewsResponse {
        private UUID id;
        private String title;
        private String content;
        private String imageUrl;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        private String authorName;

        public static NewsResponse fromEntity(News news) {
            return NewsResponse.builder()
                    .id(news.getId())
                    .title(news.getTitle())
                    .content(news.getContent())
                    .imageUrl(news.getImageUrl())
                    .createdAt(news.getCreatedAt())
                    .authorName(news.getAuthor().getFullName())
                    .build();
        }
    }
}