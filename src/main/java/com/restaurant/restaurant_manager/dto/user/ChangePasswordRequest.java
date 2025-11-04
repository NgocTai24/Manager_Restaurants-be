package com.restaurant.restaurant_manager.dto.user;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class ChangePasswordRequest {
    @NotEmpty
    private String oldPassword;
    @NotEmpty @Size(min = 6)
    private String newPassword;
}