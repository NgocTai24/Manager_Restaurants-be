package com.restaurant.restaurant_manager.dto.product;

import com.restaurant.restaurant_manager.entity.Category;
import com.restaurant.restaurant_manager.entity.Product;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private CategoryInfo category;

    @Data
    @Builder
    public static class CategoryInfo {
        private UUID id;
        private String name;
    }

    // Hàm helper để chuyển từ Entity sang DTO
    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .category(CategoryInfo.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .build())
                .build();
    }
}