package com.restaurant.restaurant_manager.dto.order;

import com.restaurant.restaurant_manager.entity.Order;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
import com.restaurant.restaurant_manager.entity.enums.PaymentMethod;
import com.restaurant.restaurant_manager.entity.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private LocalDateTime orderTime;
    private Double totalAmount;
    private OrderStatus status;
    private String note;
    private String customerName;
    private String customerPhone;

    // ✅ THÊM PAYMENT INFO
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Long payosOrderCode;
    private String paymentUrl;

    private List<OrderItemResponse> items;

    @Data
    @Builder
    public static class OrderItemResponse {
        private String productName;
        private int quantity;
        private Double price;
        private Double subTotal;
    }

    public static OrderResponse fromEntity(Order order) {
        // 1. Tạo Builder với các thông tin cơ bản của Order
        OrderResponseBuilder builder = OrderResponse.builder()
                .id(order.getId())
                .orderTime(order.getOrderTime())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .note(order.getNote())
                .customerName(order.getCustomer().getName())
                .customerPhone(order.getCustomer().getPhone())
                .paymentMethod(order.getPaymentMethod())
                .items(order.getOrderItems().stream().map(item -> OrderItemResponse.builder()
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtPurchase())
                        .subTotal(item.getPriceAtPurchase() * item.getQuantity())
                        .build()).collect(Collectors.toList()));

        // ✅ 2. KIỂM TRA PAYMENT (Tránh lỗi NullPointerException)
        // Nếu order có payment thì mới set các trường liên quan
        if (order.getPayment() != null) {
            builder.paymentStatus(order.getPayment().getStatus());
            builder.paymentUrl(order.getPayment().getPaymentUrl());
            builder.payosOrderCode(order.getPayment().getPayosOrderCode()); // Lấy từ entity Payment
        }

        // 3. Build object cuối cùng
        return builder.build();
    }
}