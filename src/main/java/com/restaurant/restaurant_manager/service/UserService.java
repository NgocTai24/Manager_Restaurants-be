package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.user.ChangePasswordRequest;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}