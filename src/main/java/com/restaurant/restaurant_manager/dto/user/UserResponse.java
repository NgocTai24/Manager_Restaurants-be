package com.restaurant.restaurant_manager.dto.user;

import com.restaurant.restaurant_manager.entity.enums.UserRole;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private UserRole role;
}