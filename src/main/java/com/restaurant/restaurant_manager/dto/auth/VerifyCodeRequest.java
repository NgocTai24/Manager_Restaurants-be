package com.restaurant.restaurant_manager.dto.auth;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class VerifyCodeRequest {
    @NotEmpty
    private String email;
    @NotEmpty
    private String code;
}