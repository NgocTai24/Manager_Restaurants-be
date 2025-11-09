package com.restaurant.restaurant_manager.dto.category;

import com.restaurant.restaurant_manager.entity.Category;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class CategoryResponse {
    private UUID id;
    private String name;

    // Hàm helper để chuyển từ Entity sang DTO
    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
