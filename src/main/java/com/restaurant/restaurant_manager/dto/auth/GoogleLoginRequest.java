package com.restaurant.restaurant_manager.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GoogleLoginRequest {

    @NotEmpty(message = "Google ID Token is required")
    private String idToken;
}