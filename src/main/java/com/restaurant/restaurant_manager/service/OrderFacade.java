package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.order.CreateOrderRequest;
import com.restaurant.restaurant_manager.dto.order.OrderResponse;
import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Đây là FACADE PATTERN.
 * Nó che giấu sự phức tạp của hệ thống con (OrderService, CustomerService, EmailService)
 * và cung cấp một giao diện đơn giản (createPublicOrder) cho Controller.
 */
@Service
@RequiredArgsConstructor
public class OrderFacade {

    // 1. Service để tìm/tạo khách
    private final CustomerService customerService;
    // 2. Service để xử lý logic order
    private final OrderService orderService;
    // 3. Service để gửi mail
    private final EmailService emailService;
    // (Bạn có thể inject thêm NotificationService, PaymentService... ở đây)

    /**
     * Phương thức Facade duy nhất mà Public Controller gọi đến
     */
    public OrderResponse createPublicOrder(CreateOrderRequest request) {

        // Bước 1: Điều phối CustomerService
        CreateOrderRequest.CustomerInfo custInfo = request.getCustomer();
        Customer customer = customerService.findOrCreateCustomer(
                custInfo.getPhone(),
                custInfo.getName(),
                custInfo.getEmail(),
                custInfo.getAddress()
        );

        // Bước 2: Điều phối OrderService
        Order order = orderService.createOrder(customer, request.getItems());

        // Bước 3: Điều phối EmailService
        if (customer.getEmail() != null) {
            String subject = "Order Confirmation #" + order.getId().toString().substring(0, 8);
            String text = "Hi " + customer.getName() + ",\nYour order (Total: " + order.getTotalAmount() + " VND) is now PENDING.";
            emailService.sendEmail(customer.getEmail(), subject, text);
        }

        // Bước 4: Chuyển đổi sang DTO
        return OrderResponse.fromEntity(order);
    }
}