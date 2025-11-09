package com.restaurant.restaurant_manager.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO Admin dùng để cập nhật thông tin Customer.
 * SĐT (phone) thường là khóa chính logic, nên chúng ta không cho đổi ở đây.
 */
@Data
public class UpdateCustomerRequest {

    @NotEmpty(message = "Customer name is required")
    private String name;

    @Email
    private String email;

    private String address;
    private LocalDate dateOfBirth;
    private String notes;
    private int loyaltyPoints;
}