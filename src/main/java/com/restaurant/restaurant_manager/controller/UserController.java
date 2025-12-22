package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.dto.user.ChangePasswordRequest;
import com.restaurant.restaurant_manager.dto.user.CreateInternalUserRequest;
import com.restaurant.restaurant_manager.dto.user.UpdateUserRequest;
import com.restaurant.restaurant_manager.dto.user.UserResponse;
import com.restaurant.restaurant_manager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- USER PROFILE APIs ---

    /**
     * User tự cập nhật thông tin cá nhân
     * PUT /api/v1/user/info
     */
    @PutMapping(value = "/user/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @ModelAttribute UpdateUserRequest request, // ✅ Đổi sang @ModelAttribute
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        UserResponse updatedUser = userService.updateMyProfile(request, file);
        return ApiResponse.success(updatedUser, "Profile updated successfully");
    }

    @PostMapping("/user/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(request);
        return ApiResponse.success(null, "Password changed successfully");
    }

    @PostMapping("/user/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> logout() {
        userService.logout();
        return ApiResponse.success(null, "Logged out successfully");
    }

    // --- ADMIN APIs ---

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ApiResponse.success(userService.getAllUsers(), "Users retrieved successfully");
    }

    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        return ApiResponse.success(userService.getUserById(id), "User retrieved successfully");
    }

    @PostMapping("/admin/users/internal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createInternalUser(
            @Valid @RequestBody CreateInternalUserRequest request
    ) {
        return ApiResponse.created(userService.createInternalUser(request), "Internal user created successfully");
    }

    /**
     * Admin cập nhật thông tin user khác (Có thể đổi Role)
     */
    @PutMapping(value = "/admin/users/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @ModelAttribute UpdateUserRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        return ApiResponse.success(userService.updateUser(id, request, file), "User updated successfully");
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ApiResponse.success(null, "User deleted successfully");
    }
}