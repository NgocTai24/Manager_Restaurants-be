package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.auth.*;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.UserRole;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;


import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${google.client-id}")
    private String GOOGLE_CLIENT_ID;

    // --- CẬP NHẬT register ---
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());
        user.setRole(UserRole.ADMIN);

        User savedUser = userRepository.save(user);

        // Tạo cả 2 token
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        // Lưu refresh token vào DB
        savedUser.setRefreshToken(refreshToken);
        savedUser.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7)); // Đồng bộ với cấu hình
        userRepository.save(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .build();
    }

    // --- CẬP NHẬT login ---
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Cập nhật refresh token mới
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    // --- MỚI: refreshToken ---
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Kiểm tra token có đúng là cái lưu trong DB không và còn hạn không
        if (!user.getRefreshToken().equals(refreshToken) ||
                user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        // Nếu hợp lệ, tạo access token mới (giữ nguyên refresh token)
        String newAccessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Trả lại refresh token cũ
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    // --- MỚI: forgotPassword ---
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null); // Không ném lỗi để bảo mật

        if (user != null) {
            String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            user.setResetPasswordToken(code);
            user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(10)); // Mã 10 phút
            userRepository.save(user);

            // Gửi email
            String text = "Your password reset code is: " + code + "\nThis code will expire in 10 minutes.";
            emailService.sendEmail(user.getEmail(), "Password Reset Code", text);
        }
        // Luôn trả về thành công để tránh lộ thông tin email nào tồn tại
    }

    // --- MỚI: verifyCode ---
    // (Hàm này có thể không cần API riêng, gộp chung vào resetPassword)
    // Nhưng nếu làm riêng, nó sẽ như sau:
    public boolean verifyCode(VerifyCodeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getResetPasswordToken() == null ||
                !user.getResetPasswordToken().equals(request.getCode()) ||
                user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invalid or expired code");
        }
        return true;
    }

    // --- MỚI: resetPassword ---
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Xác thực code
        if (!verifyCode(new VerifyCodeRequest(request.getEmail(), request.getCode()))) {
            throw new BadRequestException("Code verification failed"); // Sẽ không xảy ra nếu verifyCode ném lỗi
        }

        User user = userRepository.findByEmail(request.getEmail()).get(); // Chắc chắn có

        // 2. Đổi mật khẩu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // 3. Xóa code sau khi dùng
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);
    }

    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        try {
            // 1. Khởi tạo Google ID Token Verifier
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            // 2. Xác thực token
            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                throw new BadRequestException("Invalid Google ID Token");
            }

            // 3. Lấy thông tin (payload) từ token
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String fullName = (String) payload.get("name");

            // 4. Tìm user trong DB hoặc Tạo mới
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createNewGoogleUser(email, fullName));

            // 5. Tạo và trả về token của hệ thống (giống hệt hàm login)
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // 6. Cập nhật refresh token mới
            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
            userRepository.save(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            throw new BadRequestException("Failed to verify Google Token: " + e.getMessage());
        }
    }

    /**
     * Hàm helper để tạo user mới khi đăng nhập bằng Google
     */
    private User createNewGoogleUser(String email, String fullName) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        // Mật khẩu ngẫu nhiên (vì họ không dùng mật khẩu này để đăng nhập)
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setRole(UserRole.STAFF);

        return userRepository.save(newUser);
    }
}