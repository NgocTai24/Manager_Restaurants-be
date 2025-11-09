package com.restaurant.restaurant_manager.dto.customer;

import com.restaurant.restaurant_manager.entity.Customer;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CustomerResponse {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
    private String notes;
    private int loyaltyPoints;

    public static CustomerResponse fromEntity(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .dateOfBirth(customer.getDateOfBirth())
                .notes(customer.getNotes())
                .loyaltyPoints(customer.getLoyaltyPoints())
                .build();
    }
}