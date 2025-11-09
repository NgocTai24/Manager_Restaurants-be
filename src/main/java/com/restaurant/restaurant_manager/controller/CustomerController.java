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
@RequestMapping("/api/v1") // Base path chung
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * API Lấy tất cả khách hàng
     * GET /api/v1/admin/customers
     */
    @GetMapping("/admin/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ApiResponse.success(customers, "Customers retrieved successfully");
    }

    /**
     * API Lấy chi tiết khách hàng
     * GET /api/v1/admin/customers/{id}
     */
    @GetMapping("/admin/customers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable UUID id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ApiResponse.success(customer, "Customer retrieved successfully");
    }

    /**
     * API Cập nhật khách hàng
     * PUT /api/v1/admin/customers/{id}
     */
    @PutMapping("/admin/customers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        CustomerResponse updatedCustomer = customerService.updateCustomer(id, request);
        return ApiResponse.success(updatedCustomer, "Customer updated successfully");
    }

    /**
     * API Xóa khách hàng
     * DELETE /api/v1/admin/customers/{id}
     */
    @DeleteMapping("/admin/customers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ApiResponse.success(null, "Customer deleted successfully");
    }
}