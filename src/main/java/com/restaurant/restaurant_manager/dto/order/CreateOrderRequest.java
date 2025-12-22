package com.restaurant.restaurant_manager.dto.order;

import com.restaurant.restaurant_manager.entity.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequest {

    // Thông tin khách hàng (Chỉ dùng cho Guest/Staff đặt hộ)
    // Nếu là User đăng nhập thì trường này có thể null
    private CustomerInfo customerInfo;

    // Danh sách món ăn
    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemRequest> items;

    private String note;

    @Data
    public static class CustomerInfo {
        @NotNull(message = "Phone is required")
        private String phone;
        @NotNull(message = "Name is required")
        private String name;
        private String address;
        private String email;
    }

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod = PaymentMethod.COD;


    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private UUID productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
    }
}