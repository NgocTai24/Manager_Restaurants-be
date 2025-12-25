package com.restaurant.restaurant_manager.config.seeder;

import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CustomerSeeder {

    private final CustomerRepository customerRepository;

    @Transactional
    public void seed() {
        // Tạo khách vãng lai (Guest) - User là null
        createGuestCustomer("Le Van C", "0911222333", "guest1@gmail.com", "Hai Phong");
        createGuestCustomer("Pham Van D", "0944555666", null, "Can Tho");
        createGuestCustomer("Doan Thi E", "0977888999", "guest3@yahoo.com", "Ho Chi Minh");
    }

    private void createGuestCustomer(String name, String phone, String email, String address) {
        // Kiểm tra theo số điện thoại (Unique key của Customer)
        if (customerRepository.findByPhone(phone).isEmpty()) {
            Customer customer = new Customer();
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setAddress(address);
            customer.setLoyaltyPoints(10); // Khách vãng lai cũng có thể tích điểm
            customer.setUser(null); // <--- QUAN TRỌNG: Không có user_id

            customerRepository.save(customer);
            System.out.println("✅ Đã tạo Khách vãng lai: " + name);
        }
    }
}