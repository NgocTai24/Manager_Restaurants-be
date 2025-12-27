package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.auth.*;
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
@RequestMapping("/api/v1/auth")
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

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        AuthResponse authResponse = authService.refreshToken(request);
        return ApiResponse.success(authResponse, "Token refreshed successfully");
    }

    /**
     * API Quên Mật khẩu (Gửi code)
     * POST /api/v1/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request);
        return ApiResponse.success(null, "Password reset code sent (if email exists)");
    }

    /**
     * API Xác thực Code
     * POST /api/v1/auth/verify-code
     */
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Object>> verifyCode(
            @Valid @RequestBody VerifyCodeRequest request
    ) {
        authService.verifyCode(request);
        return ApiResponse.success(null, "Code verified successfully");
    }

    /**
     * API Đặt lại mật khẩu
     * POST /api/v1/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ApiResponse.success(null, "Password reset successfully");
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest request
    ) {
        AuthResponse authResponse = authService.loginWithGoogle(request);
        return ApiResponse.success(authResponse, "Google login successful");
    }

}