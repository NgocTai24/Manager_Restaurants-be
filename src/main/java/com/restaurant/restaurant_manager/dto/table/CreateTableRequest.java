package com.restaurant.restaurant_manager.dto.table;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTableRequest {

    @NotBlank(message = "Table name is required")
    private String name; // Ví dụ: "Bàn 01", "VIP 1"

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity; // Số ghế: 2, 4, 6...

    private String description; // Ví dụ: "Gần cửa sổ"
}