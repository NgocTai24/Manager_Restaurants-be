package com.restaurant.restaurant_manager.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
public class CreateOrderRequest {

    @Valid // Kiểm tra các thuộc tính bên trong CustomerInfo
    @NotNull
    private CustomerInfo customer;

    @Valid
    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemInfo> items;

    // Inner class cho thông tin khách hàng
    @Data
    public static class CustomerInfo {
        @NotEmpty(message = "Customer name is required")
        private String name;

        @NotEmpty(message = "Customer phone is required")
        private String phone;

        @Email(message = "Invalid email format")
        private String email;

        private String address;
    }

    // Inner class cho thông tin món hàng
    @Data
    public static class OrderItemInfo {
        @NotNull(message = "Product ID is required")
        private UUID productId;

        @NotNull(message = "Quantity is required")
        private int quantity;
    }
}