package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.customer.CustomerResponse;
import com.restaurant.restaurant_manager.dto.customer.UpdateCustomerRequest;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.dto.response.PageResponse;
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

    // GET /api/v1/staff/customers?page=0&size=10
    @GetMapping("/staff/customers")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<CustomerResponse> customers = customerService.getAllCustomers(page, size);
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


    @GetMapping("/staff/customers/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable UUID id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ApiResponse.success(customer, "Customer retrieved successfully");
    }

    @PutMapping("/staff/customers/{id}")
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