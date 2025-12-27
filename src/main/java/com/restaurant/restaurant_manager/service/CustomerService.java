package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.customer.CustomerResponse;
import com.restaurant.restaurant_manager.dto.customer.UpdateCustomerRequest;
import com.restaurant.restaurant_manager.dto.response.PageResponse;
import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer findOrCreateCustomer(String phone, String name, String email, String address) {
        return customerRepository.findByPhone(phone)
                .orElseGet(() -> {
                    if (email != null && !email.isEmpty() && customerRepository.findByEmail(email).isPresent()) {
                        throw new BadRequestException("Email already exists: " + email);
                    }
                    Customer newCustomer = new Customer();
                    newCustomer.setPhone(phone);
                    newCustomer.setName(name);
                    newCustomer.setEmail(email);
                    newCustomer.setAddress(address);
                    return customerRepository.save(newCustomer);
                });
    }

    // --- ADMIN/STAFF: Lấy tất cả khách hàng ---
    public PageResponse<CustomerResponse> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        List<CustomerResponse> content = customerPage.getContent().stream()
                .map(CustomerResponse::fromEntity)
                .collect(Collectors.toList());

        // Đóng gói vào PageResponse
        return PageResponse.<CustomerResponse>builder()
                .content(content)
                .pageNo(customerPage.getNumber())
                .pageSize(customerPage.getSize())
                .totalElements(customerPage.getTotalElements())
                .totalPages(customerPage.getTotalPages())
                .last(customerPage.isLast())
                .build();
    }

    // --- ADMIN/STAFF:
    public List<CustomerResponse> searchCustomers(String keyword) {
        return customerRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        c.getPhone().contains(keyword))
                .map(CustomerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // --- ADMIN: Lấy chi tiết ---
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return CustomerResponse.fromEntity(customer);
    }

    // --- ADMIN: Cập nhật ---
    @Transactional
    public CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                !request.getEmail().equals(customer.getEmail())) {
            if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BadRequestException("Email already exists: " + request.getEmail());
            }
        }

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNotes(request.getNotes());
        customer.setLoyaltyPoints(request.getLoyaltyPoints());

        Customer updatedCustomer = customerRepository.save(customer);
        return CustomerResponse.fromEntity(updatedCustomer);
    }

    // --- ADMIN: Xóa ---
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (!customer.getOrders().isEmpty()) {
            throw new BadRequestException("Cannot delete customer who has existing orders. Please deactivate instead.");
        }
        customerRepository.delete(customer);
    }
}