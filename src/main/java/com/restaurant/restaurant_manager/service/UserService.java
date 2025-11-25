package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.user.ChangePasswordRequest;
import com.restaurant.restaurant_manager.dto.user.CreateInternalUserRequest; // Import DTO mới
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.restaurant.restaurant_manager.dto.user.UpdateUserRequest;
import com.restaurant.restaurant_manager.dto.user.UserResponse;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Lấy user đang đăng nhập từ Security Context
    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // --- MỚI: changePassword ---
    public void changePassword(ChangePasswordRequest request) {
        User user = getAuthenticatedUser();

        // 1. Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect old password");
        }

        // 2. Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // --- MỚI: logout ---
    public void logout() {
        User user = getAuthenticatedUser();

        // Vô hiệu hóa refresh token
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
    }

    // --- ADMIN: Lấy tất cả users ---
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // --- ADMIN: Lấy user theo ID ---
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToResponse(user);
    }

    // --- ADMIN: Cập nhật user ---
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Cập nhật thông tin
        user.setFullName(request.getFullName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        return convertToResponse(userRepository.save(user));
    }

    // --- ADMIN: Xóa user ---
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // --- ADMIN: Tạo user nội bộ (Staff/Admin) ---
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createInternalUser(CreateInternalUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole()); // Role do Admin chọn (STAFF/ADMIN)
        // Các trường khác có thể để null hoặc set mặc định nếu cần

        // Chỉ lưu User, không tạo Customer -> Logic đúng như yêu cầu
        return convertToResponse(userRepository.save(user));
    }

    // --- Helper: Chuyển Entity sang DTO Response ---
    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }
}