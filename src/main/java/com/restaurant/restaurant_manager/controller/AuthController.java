package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.auth.AuthResponse;
import com.restaurant.restaurant_manager.dto.auth.LoginRequest;
import com.restaurant.restaurant_manager.dto.auth.RegisterRequest;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth") // Đây là URL public đã được khai báo trong SecurityConfig
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * API Đăng ký
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        // @Valid sẽ kích hoạt validation DTO, nếu lỗi sẽ bị GlobalExceptionHandler bắt
        AuthResponse authResponse = authService.register(request);

        // Trả về response chuẩn 201 Created
        return ApiResponse.created(authResponse, "User registered successfully");
    }

    /**
     * API Đăng nhập
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse authResponse = authService.login(request);

        // Trả về response chuẩn 200 OK
        return ApiResponse.success(authResponse, "User logged in successfully");
    }
}