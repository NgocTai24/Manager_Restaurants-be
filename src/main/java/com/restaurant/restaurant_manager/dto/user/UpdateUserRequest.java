package com.restaurant.restaurant_manager.dto.user;

import com.restaurant.restaurant_manager.entity.enums.UserRole;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotEmpty
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private UserRole role;
}