package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.customer.CustomerResponse;
import com.restaurant.restaurant_manager.dto.customer.UpdateCustomerRequest;
import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
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

    /**
     * [ADMIN] Lấy tất cả khách hàng
     */
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(CustomerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * [ADMIN] Lấy chi tiết khách hàng
     */
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return CustomerResponse.fromEntity(customer);
    }

    /**
     * [ADMIN] Cập nhật khách hàng
     */
    @Transactional
    public CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Kiểm tra email nếu email bị thay đổi
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

    /**
     * [ADMIN] Xóa khách hàng
     */
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // (Kiểm tra logic nghiệp vụ, ví dụ: không cho xóa nếu có đơn hàng)
        // Lưu ý: Cần kiểm tra @Data trên Customer và Order/Reservation
        // để đảm bảo không bị lỗi LazyInitializationException

        // Tạm thời comment logic kiểm tra phức tạp
        // if (!customer.getOrders().isEmpty() || !customer.getReservations().isEmpty()) {
        //     throw new BadRequestException("Cannot delete customer with existing orders or reservations.");
        // }

        customerRepository.delete(customer);
    }
}