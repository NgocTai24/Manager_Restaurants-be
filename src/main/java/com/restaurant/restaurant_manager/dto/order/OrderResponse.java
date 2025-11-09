package com.restaurant.restaurant_manager.dto.order;

import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.entity.Order;
import com.restaurant.restaurant_manager.entity.OrderItem;
import com.restaurant.restaurant_manager.entity.Product;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO trả về chi tiết một Order (đã giải quyết vòng lặp)
 */
@Data
@Builder
public class OrderResponse {
    private UUID id;
    private LocalDateTime orderTime;
    private Double totalAmount;
    private OrderStatus status;
    private CustomerInfo customer;
    private List<OrderItemInfo> items;

    // Inner class cho thông tin khách hàng
    @Data
    @Builder
    public static class CustomerInfo {
        private UUID id;
        private String name;
        private String phone;
        private String address;
    }

    // Inner class cho thông tin món hàng
    @Data
    @Builder
    public static class OrderItemInfo {
        private UUID id;
        private int quantity;
        private Double priceAtPurchase;
        private ProductInfo product;
    }

    // Inner class cho thông tin sản phẩm (tối giản)
    @Data
    @Builder
    public static class ProductInfo {
        private UUID id;
        private String name;
        private String imageUrl;
    }

    // Hàm helper để chuyển từ Entity sang DTO
    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderTime(order.getOrderTime())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .customer(CustomerInfo.builder()
                        .id(order.getCustomer().getId())
                        .name(order.getCustomer().getName())
                        .phone(order.getCustomer().getPhone())
                        .address(order.getCustomer().getAddress())
                        .build())
                .items(order.getOrderItems().stream()
                        .map(OrderResponse::convertOrderItem)
                        .collect(Collectors.toList()))
                .build();
    }

    private static OrderItemInfo convertOrderItem(OrderItem item) {
        return OrderItemInfo.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .product(ProductInfo.builder()
                        .id(item.getProduct().getId())
                        .name(item.getProduct().getName())
                        .imageUrl(item.getProduct().getImageUrl())
                        .build())
                .build();
    }
}