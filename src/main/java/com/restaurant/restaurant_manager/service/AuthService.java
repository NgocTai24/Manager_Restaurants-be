package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.auth.AuthResponse;
import com.restaurant.restaurant_manager.dto.auth.LoginRequest;
import com.restaurant.restaurant_manager.dto.auth.RegisterRequest;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.UserRole;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Xử lý logic Đăng ký
     */
    public AuthResponse register(RegisterRequest request) {
        // 1. Kiểm tra xem email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        // 2. Tạo User mới
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Mã hóa mật khẩu
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());

        // --- QUAN TRỌNG: Gán vai trò (Role) ---
        // Mặc định tất cả user đăng ký mới là STAFF
        // Bạn có thể thay đổi logic này nếu muốn (ví dụ: cần admin duyệt)
        user.setRole(UserRole.STAFF);

        // 3. Lưu vào DB
        User savedUser = userRepository.save(user);

        // 4. Tạo token
        String jwtToken = jwtService.generateToken(savedUser);

        // 5. Trả về response
        return AuthResponse.builder()
                .token(jwtToken)
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .build();
    }

    /**
     * Xử lý logic Đăng nhập
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Xác thực người dùng (quan trọng)
        // Spring Security sẽ tự động kiểm tra email và password
        // Nếu sai, nó sẽ ném ra AuthenticationException (được xử lý bởi EntryPoint)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Nếu xác thực thành công, tìm lại user (chắc chắn có)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found after authentication")); // Lỗi này không nên xảy ra

        // 3. Tạo token
        String jwtToken = jwtService.generateToken(user);

        // 4. Trả về response
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}