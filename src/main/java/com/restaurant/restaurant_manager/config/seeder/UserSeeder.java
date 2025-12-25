package com.restaurant.restaurant_manager.config.seeder;

import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.UserRole;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import com.restaurant.restaurant_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserSeeder {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void seed() {
        // 1. Tạo Admin (User Only)
        createInternalUser("admin@gmail.com", "admin123", "Super Admin", UserRole.ADMIN);

        // 2. Tạo Staff (User Only)
        createInternalUser("staff@gmail.com", "staff123", "Nhan Vien 1", UserRole.STAFF);

        // 3. Tạo Khách hàng CÓ TÀI KHOẢN (User + Customer Linked)
        createRegisteredCustomer("user1@gmail.com", "123456", "Nguyen Van A", "0901234567", "Hanoi");
        createRegisteredCustomer("user2@gmail.com", "123456", "Tran Thi B", "0909876543", "Da Nang");
    }

    // Hàm tạo User nội bộ (Admin/Staff) - Không cần tạo Customer profile
    private void createInternalUser(String email, String password, String name, UserRole role) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFullName(name);
            user.setRole(role);
            userRepository.save(user);
            System.out.println("✅ Đã tạo User nội bộ: " + email);
        }
    }

    // Hàm tạo Khách hàng đăng ký (Giống logic Register)
    private void createRegisteredCustomer(String email, String password, String name, String phone, String address) {
        if (!userRepository.existsByEmail(email)) {
            // B1: Tạo User
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFullName(name);
            user.setRole(UserRole.CUSTOMER);
            user.setAddress(address);

            User savedUser = userRepository.save(user);

            // B2: Tạo Customer và Link với User
            Customer customer = new Customer();
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setAddress(address);
            customer.setUser(savedUser); // <--- QUAN TRỌNG: Link user_id
            customer.setLoyaltyPoints(0);

            customerRepository.save(customer);
            System.out.println("✅ Đã tạo Khách hàng có tài khoản: " + email);
        }
    }
}