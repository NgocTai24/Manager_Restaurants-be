package com.restaurant.restaurant_manager.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateCustomerRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    private String address;
    private LocalDate dateOfBirth;
    private String notes;
    private int loyaltyPoints;
}