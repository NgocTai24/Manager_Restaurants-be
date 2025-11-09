package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.order.CreateOrderRequest;
import com.restaurant.restaurant_manager.dto.order.OrderResponse;
import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.entity.Order;
import com.restaurant.restaurant_manager.entity.OrderItem;
import com.restaurant.restaurant_manager.entity.Product;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.OrderItemRepository;
import com.restaurant.restaurant_manager.repository.OrderRepository;
import com.restaurant.restaurant_manager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(Customer customer, List<CreateOrderRequest.OrderItemInfo> itemInfos) {

        // 1. Lấy danh sách Product ID
        List<UUID> productIds = itemInfos.stream()
                .map(CreateOrderRequest.OrderItemInfo::getProductId)
                .collect(Collectors.toList());

        // 2. Lấy tất cả Product từ DB chỉ bằng một query
        Map<UUID, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        // 3. Tính toán tổng tiền
        double totalAmount = 0.0;
        Set<OrderItem> orderItems = new HashSet<>();

        for (CreateOrderRequest.OrderItemInfo itemInfo : itemInfos) {
            Product product = productMap.get(itemInfo.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found with id: " + itemInfo.getProductId());
            }

            totalAmount += (product.getPrice() * itemInfo.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemInfo.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItems.add(orderItem);
        }

        // 4. Tạo và Lưu Order
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderTime(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // 5. Gán Order vào OrderItems và Lưu
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(orderItems);

        // ✅ 6. LOAD LẠI ORDER VỚI OrderItems (QUAN TRỌNG!)
        // Cách 1: findById với JOIN FETCH
        return orderRepository.findByIdWithItems(savedOrder.getId())
                .orElseGet(() -> {
                    // Fallback: Load lại thủ công
                    Order reloadedOrder = orderRepository.findById(savedOrder.getId()).get();
                    reloadedOrder.setOrderItems(orderItems); // Set manually
                    return reloadedOrder;
                });
    }

    // --- Các API cho Admin ---

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(updatedOrder);
    }
}