package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.customer.CustomerResponse;
import com.restaurant.restaurant_manager.dto.customer.UpdateCustomerRequest;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // --- 1. Lấy tất cả (Dành cho Admin/Staff) ---
    @GetMapping("/staff/customers")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ApiResponse.success(customers, "Customers retrieved successfully");
    }

    // --- 2. Tìm kiếm (Quan trọng: Tìm theo SĐT/Tên) ---
    @GetMapping("/staff/customers/search")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> searchCustomers(
            @RequestParam String keyword
    ) {
        List<CustomerResponse> customers = customerService.searchCustomers(keyword);
        return ApiResponse.success(customers, "Search results");
    }

    // --- 3. Lấy chi tiết ---
    @GetMapping("/staff/customers/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable UUID id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ApiResponse.success(customer, "Customer retrieved successfully");
    }

    // --- 4. Cập nhật (Chỉ Admin hoặc Staff quản lý) ---
    @PutMapping("/staff/customers/{id}") // Staff có thể sửa thông tin cơ bản
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        CustomerResponse updatedCustomer = customerService.updateCustomer(id, request);
        return ApiResponse.success(updatedCustomer, "Customer updated successfully");
    }

    // --- 5. Xóa (Chỉ ADMIN) ---
    @DeleteMapping("/admin/customers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ApiResponse.success(null, "Customer deleted successfully");
    }
}