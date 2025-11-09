package com.restaurant.restaurant_manager.dto.category;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotEmpty(message = "Category name is required")
    private String name;
}