package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.dto.user.ChangePasswordRequest;
import com.restaurant.restaurant_manager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// API này YÊU CẦU ĐĂNG NHẬP
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * API Đổi mật khẩu (cho user đã đăng nhập)
     * POST /api/v1/user/change-password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(request);
        return ApiResponse.success(null, "Password changed successfully");
    }

    /**
     * API Đăng xuất
     * POST /api/v1/user/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout() {
        userService.logout();
        return ApiResponse.success(null, "Logged out successfully");
    }
}