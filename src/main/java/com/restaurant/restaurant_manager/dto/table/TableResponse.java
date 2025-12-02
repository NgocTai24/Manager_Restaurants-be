package com.restaurant.restaurant_manager.dto.table;

import com.restaurant.restaurant_manager.entity.RestaurantTable;
import com.restaurant.restaurant_manager.entity.enums.TableStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TableResponse {
    private UUID id;
    private String name;
    private int capacity;
    private TableStatus status;
    private String description;

    public static TableResponse fromEntity(RestaurantTable table) {
        return TableResponse.builder()
                .id(table.getId())
                .name(table.getName())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .description(table.getDescription())
                .build();
    }
}