package com.restaurant.restaurant_manager.dto.customer;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomerResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
}
