package com.restaurant.restaurant_manager.dto.auth;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
@Data
public class RefreshTokenRequest {
    @NotEmpty
    private String refreshToken;
}