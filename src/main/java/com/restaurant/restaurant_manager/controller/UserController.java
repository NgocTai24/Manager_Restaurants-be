package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.dto.user.ChangePasswordRequest;
import com.restaurant.restaurant_manager.dto.user.UpdateUserRequest;
import com.restaurant.restaurant_manager.dto.user.UserResponse;
import com.restaurant.restaurant_manager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")  // ✅ Base path chung
@RequiredArgsConstructor
// ❌ BỎ @PreAuthorize ở class level
public class UserController {

    private final UserService userService;

    /**
     * API Đổi mật khẩu (cho user đã đăng nhập)
     * POST /api/v1/user/change-password
     */
    @PostMapping("/user/change-password")
    @PreAuthorize("isAuthenticated()") // ✅ Chỉ cần đăng nhập
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
    @PostMapping("/user/logout")
    @PreAuthorize("isAuthenticated()") // ✅ Chỉ cần đăng nhập
    public ResponseEntity<ApiResponse<Object>> logout() {
        userService.logout();
        return ApiResponse.success(null, "Logged out successfully");
    }

    /**
     * Lấy danh sách tất cả users
     * GET /api/v1/admin/users
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')") // ✅ Bắt buộc ADMIN
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ApiResponse.success(users, "Users retrieved successfully");
    }

    /**
     * Lấy chi tiết user theo ID
     * GET /api/v1/admin/users/{id}
     */
    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')") // ✅ Bắt buộc ADMIN
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        return ApiResponse.success(user, "User retrieved successfully");
    }

    /**
     * Cập nhật user
     * PUT /api/v1/admin/users/{id}
     */
    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')") // ✅ Bắt buộc ADMIN
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ApiResponse.success(updatedUser, "User updated successfully");
    }

    /**
     * Xóa user
     * DELETE /api/v1/admin/users/{id}
     */
    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')") // ✅ Bắt buộc ADMIN
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ApiResponse.success(null, "User deleted successfully");
    }
}