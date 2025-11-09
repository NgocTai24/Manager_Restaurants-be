package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer findOrCreateCustomer(String phone, String name, String email, String address) {
        // Tìm theo phone trước
        return customerRepository.findByPhone(phone)
                .orElseGet(() -> {
                    // ✅ Kiểm tra email đã tồn tại chưa
                    if (email != null && customerRepository.findByEmail(email).isPresent()) {
                        throw new BadRequestException("Email already exists: " + email);
                    }

                    // Tạo mới
                    Customer newCustomer = new Customer();
                    newCustomer.setPhone(phone);
                    newCustomer.setName(name);
                    newCustomer.setEmail(email);
                    newCustomer.setAddress(address);
                    return customerRepository.save(newCustomer);
                });
    }
}