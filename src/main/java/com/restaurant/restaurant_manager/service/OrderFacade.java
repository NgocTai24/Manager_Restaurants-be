package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.order.CreateOrderRequest;
import com.restaurant.restaurant_manager.dto.order.OrderResponse;
import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.entity.Order;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * FACADE PATTERN:
 * Che giấu sự phức tạp của việc tìm kiếm Customer, tính toán Order, và gửi Email.
 * Controller chỉ cần gọi 1 hàm duy nhất.
 */
@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    /**
     * Case 1: Đặt hàng cho User đã đăng nhập (Authenticated)
     */
    @Transactional
    public OrderResponse placeOrderForUser(User user, CreateOrderRequest request) {
        // 1. Tìm Customer thông qua User đã đăng nhập
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Customer profile not found for this user."));

        // 2. Tạo đơn hàng
        Order order = orderService.createOrder(customer, request.getItems(), request.getNote());

        // 3. Gửi email xác nhận (Async nếu có thể)
        sendOrderConfirmationEmail(customer, order);

        return OrderResponse.fromEntity(order);
    }

    /**
     * Case 2: Đặt hàng cho Khách vãng lai / Staff đặt giúp (Guest)
     */
    @Transactional
    public OrderResponse placeOrderForGuest(CreateOrderRequest request) {
        if (request.getCustomerInfo() == null) {
            throw new BadRequestException("Customer info is required for guest orders");
        }

        // 1. Tìm hoặc tạo Customer mới dựa trên SĐT
        Customer customer = customerService.findOrCreateCustomer(
                request.getCustomerInfo().getPhone(),
                request.getCustomerInfo().getName(),
                request.getCustomerInfo().getEmail(),
                request.getCustomerInfo().getAddress()
        );

        // 2. Tạo đơn hàng
        Order order = orderService.createOrder(customer, request.getItems(), request.getNote());

        // 3. Gửi email (nếu có email)
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            sendOrderConfirmationEmail(customer, order);
        }

        return OrderResponse.fromEntity(order);
    }

    // --- MỚI: CASE 3: ADMIN CẬP NHẬT TRẠNG THÁI ---
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus status) {
        // 1. Gọi Service để update DB
        Order updatedOrder = orderService.updateStatus(orderId, status);

        // 2. Gửi email thông báo trạng thái mới (nếu khách có email)
        Customer customer = updatedOrder.getCustomer();
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            sendStatusUpdateEmail(customer, updatedOrder);
        }

        return OrderResponse.fromEntity(updatedOrder);
    }

    // --- MỚI: LOGIC HỦY ĐƠN CHO KHÁCH HÀNG (USER) ---
    @Transactional
    public OrderResponse cancelOrderForUser(User user, UUID orderId) {
        Order order = orderService.findOrderById(orderId);

        // 1. Kiểm tra quyền sở hữu: Đơn này có phải của User này không?
        Customer customer = order.getCustomer();
        if (customer.getUser() == null || !customer.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You do not have permission to cancel this order");
        }

        // 2. Kiểm tra trạng thái: Chỉ được hủy khi đang PENDING
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order cannot be cancelled because it is " + order.getStatus());
        }

        // 3. Kiểm tra thời gian: Chỉ được hủy trong vòng 10 phút
        long minutesElapsed = ChronoUnit.MINUTES.between(order.getOrderTime(), LocalDateTime.now());
        if (minutesElapsed > 10) {
            throw new BadRequestException("Order cannot be cancelled after 10 minutes");
        }

        // 4. Thực hiện hủy
        Order cancelledOrder = orderService.updateStatus(orderId, OrderStatus.CANCELLED);

        // 5. Gửi mail thông báo
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            sendStatusUpdateEmail(customer, cancelledOrder);
        }

        return OrderResponse.fromEntity(cancelledOrder);
    }

    // --- MỚI: LOGIC HỦY ĐƠN CHO STAFF ---
    @Transactional
    public OrderResponse cancelOrderForStaff(UUID orderId) {
        Order order = orderService.findOrderById(orderId);

        // Staff có quyền hủy mạnh hơn, nhưng không nên hủy đơn đã hoàn thành
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a COMPLETED order");
        }

        Order cancelledOrder = orderService.updateStatus(orderId, OrderStatus.CANCELLED);

        // Gửi mail báo khách
        if (order.getCustomer().getEmail() != null) {
            sendStatusUpdateEmail(order.getCustomer(), cancelledOrder);
        }

        return OrderResponse.fromEntity(cancelledOrder);
    }

    private void sendOrderConfirmationEmail(Customer customer, Order order) {
        try {
            String subject = "Order Confirmation #" + order.getId().toString().substring(0, 8);
            String content = "Hello " + customer.getName() + ",\n"
                    + "Your order has been placed successfully.\n"
                    + "Total Amount: " + order.getTotalAmount() + "\n"
                    + "Status: " + order.getStatus();
            emailService.sendEmail(customer.getEmail(), subject, content);
        } catch (Exception e) {
            // Log error nhưng không chặn luồng order
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    private void sendStatusUpdateEmail(Customer customer, Order order) {
        try {
            String subject = "Order Update #" + order.getId().toString().substring(0, 8);
            String text = "Hi " + customer.getName() + ",\n"
                    + "Your order status has been updated to: " + order.getStatus() + ".\n"
                    + "Thank you for dining with us!";
            emailService.sendEmail(customer.getEmail(), subject, text);
        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
        }
    }
}