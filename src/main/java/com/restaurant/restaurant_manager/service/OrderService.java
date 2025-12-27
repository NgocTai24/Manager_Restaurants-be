package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.order.CreateOrderRequest;
import com.restaurant.restaurant_manager.dto.order.OrderResponse;
import com.restaurant.restaurant_manager.dto.response.PageResponse;
import com.restaurant.restaurant_manager.entity.*;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
import com.restaurant.restaurant_manager.entity.enums.PaymentMethod;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.OrderRepository;
import com.restaurant.restaurant_manager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(Customer customer, List<CreateOrderRequest.OrderItemRequest> itemsRequest, String note, PaymentMethod paymentMethod) {
        // 1. Lấy danh sách Product ID
        List<UUID> productIds = itemsRequest.stream()
                .map(CreateOrderRequest.OrderItemRequest::getProductId)
                .collect(Collectors.toList());

        Map<UUID, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 2. Tạo Order Entity
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setNote(note);
        order.setPaymentMethod(paymentMethod);

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        // 3. Xử lý từng item
        for (CreateOrderRequest.OrderItemRequest itemReq : itemsRequest) {
            Product product = productMap.get(itemReq.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found with id: " + itemReq.getProductId());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            totalAmount += (product.getPrice() * itemReq.getQuantity());
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        // 4. Lưu tất cả
        return orderRepository.save(order);
    }

    public PageResponse<OrderResponse> getAllOrders(int page, int size) {
        // Sắp xếp theo thời gian đặt hàng mới nhất lên đầu (DESC)
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderTime").descending());

        Page<Order> orderPage = orderRepository.findAll(pageable);

        // Convert Entity -> DTO
        List<OrderResponse> content = orderPage.getContent().stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        // Đóng gói vào PageResponse
        return PageResponse.<OrderResponse>builder()
                .content(content)
                .pageNo(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }

    @Transactional
    public Order updateStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // (Optional) Logic kiểm tra luồng trạng thái
        // if (order.getStatus() == OrderStatus.CANCELLED) { ... }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public Order findOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
}