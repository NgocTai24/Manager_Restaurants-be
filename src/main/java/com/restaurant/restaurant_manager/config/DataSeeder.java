package com.restaurant.restaurant_manager.config;

import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.UserRole;
import com.restaurant.restaurant_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class này sẽ chạy tự động khi ứng dụng khởi động (nhờ implements CommandLineRunner)
 * Dùng để tạo tài khoản Admin và Staff mẫu ban đầu.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setFullName("Super Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            admin.setAddress("Hanoi, Vietnam");

            userRepository.save(admin);
            System.out.println("✅ Đã khởi tạo tài khoản ADMIN: admin@gmail.com / admin123");
        }

        // 2. Tạo tài khoản STAFF (nếu chưa có)
        if (userRepository.findByEmail("staff@gmail.com").isEmpty()) {
            User staff = new User();
            staff.setFullName("Nhan Vien 1");
            staff.setEmail("staff@gmail.com");
            staff.setPassword(passwordEncoder.encode("staff123"));
            staff.setRole(UserRole.STAFF);
            staff.setAddress("Hanoi, Vietnam");

            userRepository.save(staff);
            System.out.println("✅ Đã khởi tạo tài khoản STAFF: staff@gmail.com / staff123");
        }
    }
}

