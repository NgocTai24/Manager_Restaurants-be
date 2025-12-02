package com.restaurant.restaurant_manager.dto.table;

import com.restaurant.restaurant_manager.entity.enums.TableStatus;
import lombok.Data;

@Data
public class UpdateTableRequest {
    private String name;
    private Integer capacity;
    private String description;
    private TableStatus status; // Cho phép Admin update trạng thái thủ công (vd: Bảo trì)
}