package com.restaurant.restaurant_manager.config;

import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.UserRole;
import com.restaurant.restaurant_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategorySeeder categorySeeder;
    private final TableSeeder tableSeeder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🚀 Bắt đầu khởi tạo dữ liệu mẫu...");

        // 2. Chạy Seeder cho User (Logic cũ của bạn)
        seedUsers();
        categorySeeder.seed();
        tableSeeder.seed();
        // productSeeder.seed();

        System.out.println("🏁 Hoàn tất khởi tạo dữ liệu.");
    }

    // Tách logic User xuống đây cho gọn (Hoặc tốt nhất là chuyển sang UserSeeder.java)
    private void seedUsers() {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setFullName("Super Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            admin.setAddress("Hanoi, Vietnam");
            userRepository.save(admin);
            System.out.println("✅ User Seeder: Đã tạo Admin");
        }

        if (userRepository.findByEmail("staff@gmail.com").isEmpty()) {
            User staff = new User();
            staff.setFullName("Nhan Vien 1");
            staff.setEmail("staff@gmail.com");
            staff.setPassword(passwordEncoder.encode("staff123"));
            staff.setRole(UserRole.STAFF);
            staff.setAddress("Hanoi, Vietnam");
            userRepository.save(staff);
            System.out.println("✅ User Seeder: Đã tạo Staff");
        }
    }
}