package com.restaurant.restaurant_manager.dto.auth;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class ResetPasswordRequest {
    @NotEmpty
    private String email;
    @NotEmpty
    private String code;
    @NotEmpty @Size(min = 6)
    private String newPassword;
}