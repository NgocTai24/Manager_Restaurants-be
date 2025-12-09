package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.user.ChangePasswordRequest;
import com.restaurant.restaurant_manager.dto.user.CreateInternalUserRequest;
import com.restaurant.restaurant_manager.dto.user.UpdateUserRequest;
import com.restaurant.restaurant_manager.dto.user.UserResponse;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void changePassword(ChangePasswordRequest request) {
        User user = getAuthenticatedUser();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void logout() {
        User user = getAuthenticatedUser();
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
    }

    // --- ADMIN: Lấy tất cả ---
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // --- ADMIN: Lấy theo ID ---
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToResponse(user);
    }

    // --- ADMIN: Cập nhật user bất kỳ (Theo ID) ---
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Admin có quyền sửa Role
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        mapRequestToUser(user, request); // Cập nhật thông tin chung

        return convertToResponse(userRepository.save(user));
    }

    // --- MỚI: USER TỰ CẬP NHẬT (Lấy từ Token) ---
    // Không cần truyền ID, không cần check Role Admin
    public UserResponse updateMyProfile(UpdateUserRequest request) {
        User user = getAuthenticatedUser(); // Lấy chính mình

        // User KHÔNG được phép tự sửa Role của mình -> Không set Role ở đây

        mapRequestToUser(user, request); // Cập nhật thông tin chung

        // Lưu lại (Lưu ý: Nếu user này có Customer liên kết, có thể cần đồng bộ thông tin sang bảng Customer nếu muốn)
        return convertToResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createInternalUser(CreateInternalUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        return convertToResponse(userRepository.save(user));
    }

    // --- Helper ---
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

    // Helper: Map dữ liệu chung (tránh lặp code)
    private void mapRequestToUser(User user, UpdateUserRequest request) {
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
    }
}