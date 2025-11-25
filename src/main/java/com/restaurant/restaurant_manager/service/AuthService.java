package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.auth.*;
import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.UserRole;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import com.restaurant.restaurant_manager.repository.UserRepository;
import jakarta.transaction.Transactional; // Import Transactional
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
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${google.client-id}")
    private String GOOGLE_CLIENT_ID;

    // --- CẬP NHẬT register: Thêm @Transactional để đảm bảo lưu cả 2 hoặc không lưu gì cả ---
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        // Kiểm tra Phone bên Customer nữa (Vì phone là unique)
        if (customerRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new BadRequestException("Phone number already used by another customer");
        }

        // 1. Tạo User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());
        user.setRole(UserRole.CUSTOMER);

        User savedUser = userRepository.save(user);

        // 2. TỰ ĐỘNG TẠO CUSTOMER
        Customer customer = new Customer();
        customer.setName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setDateOfBirth(request.getDateOfBirth());
        // LƯU Ý: RegisterRequest cần có getPhone()
        customer.setPhone(request.getPhone());

        // Liên kết với User vừa tạo
        customer.setUser(savedUser);

        customerRepository.save(customer);

        // 3. Tạo Token
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        savedUser.setRefreshToken(refreshToken);
        savedUser.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .build();
    }

    // --- login giữ nguyên ---
    public AuthResponse login(LoginRequest request) {
        // ... (Giữ nguyên code cũ của bạn) ...
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

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

    // --- refreshToken, forgotPassword, verifyCode, resetPassword giữ nguyên ---
    // (Tôi lược bỏ để ngắn gọn, bạn giữ nguyên code cũ)
    public AuthResponse refreshToken(RefreshTokenRequest request) { /* Code cũ */ return null; }
    public void forgotPassword(ForgotPasswordRequest request) { /* Code cũ */ }
    public boolean verifyCode(VerifyCodeRequest request) { /* Code cũ */ return true; }
    public void resetPassword(ResetPasswordRequest request) { /* Code cũ */ }


    // --- CẬP NHẬT loginWithGoogle ---
    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                throw new BadRequestException("Invalid Google ID Token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String fullName = (String) payload.get("name");

            // Tìm user, nếu chưa có thì tạo mới
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createNewGoogleUser(email, fullName));

            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

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

    private User createNewGoogleUser(String email, String fullName) {
        // 1. Tạo User
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setRole(UserRole.CUSTOMER); // Google login thường là Customer
        User savedUser = userRepository.save(newUser);

        // 2. Tạo Customer
        // VẤN ĐỀ: Customer.phone đang required (nullable=false)
        // Bạn cần xử lý logic:
        // Cách 1: Cho phép Customer.phone null (sửa entity)
        // Cách 2: Sinh số tạm
        Customer newCustomer = new Customer();
        newCustomer.setEmail(email);
        newCustomer.setName(fullName);
        newCustomer.setUser(savedUser);

        // TẠM THỜI: Để trống nếu bạn sửa entity Customer thành nullable=true cho phone
        // Hoặc set giá trị tạm để pass qua DB constraint
        newCustomer.setPhone("GOOGLE_" + UUID.randomUUID().toString().substring(0,8));

        customerRepository.save(newCustomer);

        return savedUser;
    }
}