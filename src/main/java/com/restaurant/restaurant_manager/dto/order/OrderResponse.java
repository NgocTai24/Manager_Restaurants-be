package com.restaurant.restaurant_manager.dto.order;

import com.restaurant.restaurant_manager.entity.Order;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
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
        return OrderResponse.builder()
                .id(order.getId())
                .orderTime(order.getOrderTime())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .note(order.getNote())
                .customerName(order.getCustomer().getName())
                .customerPhone(order.getCustomer().getPhone())
                .items(order.getOrderItems().stream().map(item -> OrderItemResponse.builder()
                        .productName(item.getProduct().getName()) // Giả sử Product có getName
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtPurchase())
                        .subTotal(item.getPriceAtPurchase() * item.getQuantity())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}