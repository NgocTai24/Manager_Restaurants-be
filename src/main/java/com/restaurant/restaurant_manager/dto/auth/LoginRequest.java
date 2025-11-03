package com.restaurant.restaurant_manager.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {

    @NotEmpty(message = "Email không tồn tại")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotEmpty(message = "Password không tồn tại")
    private String password;
}