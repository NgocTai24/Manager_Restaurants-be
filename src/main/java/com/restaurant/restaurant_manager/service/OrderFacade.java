package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.order.CreateOrderRequest;
import com.restaurant.restaurant_manager.dto.order.OrderResponse;
import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.entity.Order;
import com.restaurant.restaurant_manager.entity.Payment;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
import com.restaurant.restaurant_manager.entity.enums.PaymentMethod;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final PaymentService paymentService;

    /**
     * Case 1: Đặt hàng cho User đã đăng nhập (Authenticated)
     */
    @Transactional
    public OrderResponse placeOrderForUser(User user, CreateOrderRequest request) {
        // 1. Tìm Customer
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Customer profile not found for this user."));

        // 2. Tạo Order
        Order order = orderService.createOrder(
                customer,
                request.getItems(),
                request.getNote(),
                request.getPaymentMethod()
        );

        // 3. Tạo Payment (Gọi PayOS)
        Payment payment = paymentService.createPayment(order);

        // 4. Gửi email
        sendOrderConfirmationEmail(customer, order, payment);

        // 5. Trả về response
        OrderResponse response = OrderResponse.fromEntity(order);
        // Map dữ liệu Payment sang Response thủ công để đảm bảo có dữ liệu ngay lập tức
        if (payment != null) {
            response.setPaymentUrl(payment.getPaymentUrl());
            response.setPaymentStatus(payment.getStatus());
            response.setPayosOrderCode(payment.getPayosOrderCode()); // ✅ Quan trọng để test Webhook
        }

        return response;
    }

    /**
     * Case 2: Đặt hàng cho Khách vãng lai
     */
    @Transactional
    public OrderResponse placeOrderForGuest(CreateOrderRequest request) {
        if (request.getCustomerInfo() == null) {
            throw new BadRequestException("Customer info is required for guest orders");
        }

        // 1. Tìm hoặc tạo Customer
        Customer customer = customerService.findOrCreateCustomer(
                request.getCustomerInfo().getPhone(),
                request.getCustomerInfo().getName(),
                request.getCustomerInfo().getEmail(),
                request.getCustomerInfo().getAddress()
        );

        // 2. Tạo Order
        Order order = orderService.createOrder(
                customer,
                request.getItems(),
                request.getNote(),
                request.getPaymentMethod()
        );

        // 3. Tạo Payment
        Payment payment = paymentService.createPayment(order);

        // 4. Gửi email
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            sendOrderConfirmationEmail(customer, order, payment);
        }

        // 5. Trả về response
        OrderResponse response = OrderResponse.fromEntity(order);
        if (payment != null) {
            response.setPaymentUrl(payment.getPaymentUrl());
            response.setPaymentStatus(payment.getStatus());
            response.setPayosOrderCode(payment.getPayosOrderCode()); // ✅ Quan trọng để test Webhook
        }

        return response;
    }

    // ... Các method updateOrderStatus, cancelOrder... giữ nguyên như cũ của bạn ...
    /**
     * Case 3: ADMIN Cập nhật trạng thái
     */
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus status) {
        Order updatedOrder = orderService.updateStatus(orderId, status);
        Customer customer = updatedOrder.getCustomer();
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            sendStatusUpdateEmail(customer, updatedOrder);
        }
        return OrderResponse.fromEntity(updatedOrder);
    }

    /**
     * Case 4: USER hủy đơn
     */
    @Transactional
    public OrderResponse cancelOrderForUser(User user, UUID orderId) {
        Order order = orderService.findOrderById(orderId);
        Customer customer = order.getCustomer();
        if (customer.getUser() == null || !customer.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You do not have permission to cancel this order");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order cannot be cancelled because it is " + order.getStatus());
        }
        long minutesElapsed = ChronoUnit.MINUTES.between(order.getOrderTime(), LocalDateTime.now());
        if (minutesElapsed > 10) {
            throw new BadRequestException("Order cannot be cancelled after 10 minutes");
        }
        Order cancelledOrder = orderService.updateStatus(orderId, OrderStatus.CANCELLED);
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            sendStatusUpdateEmail(customer, cancelledOrder);
        }
        return OrderResponse.fromEntity(cancelledOrder);
    }

    /**
     * Case 5: STAFF hủy đơn
     */
    @Transactional
    public OrderResponse cancelOrderForStaff(UUID orderId) {
        Order order = orderService.findOrderById(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a COMPLETED order");
        }
        Order cancelledOrder = orderService.updateStatus(orderId, OrderStatus.CANCELLED);
        if (order.getCustomer().getEmail() != null) {
            sendStatusUpdateEmail(order.getCustomer(), cancelledOrder);
        }
        return OrderResponse.fromEntity(cancelledOrder);
    }

    // ========== PRIVATE HELPER METHODS ==========

    private void sendOrderConfirmationEmail(Customer customer, Order order, Payment payment) {
        try {
            String subject = "Order Confirmation #" + order.getId().toString().substring(0, 8);

            StringBuilder content = new StringBuilder();
            content.append("Hello ").append(customer.getName()).append(",\n\n");
            content.append("Your order has been placed successfully.\n");
            content.append("Total Amount: ").append(order.getTotalAmount()).append(" VND\n");
            content.append("Payment Method: ").append(order.getPaymentMethod()).append("\n");
            content.append("Status: ").append(order.getStatus()).append("\n\n");

            if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER && payment != null && payment.getPaymentUrl() != null) {
                content.append("Please complete your payment here:\n");
                content.append(payment.getPaymentUrl()).append("\n\n");
            }

            content.append("Thank you for your order!");

            emailService.sendEmail(customer.getEmail(), subject, content.toString());
        } catch (Exception e) {
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

    /**
     * Lấy chi tiết đơn hàng (Dành cho Admin/Staff/User)
     * Convert từ Entity -> DTO đầy đủ thông tin
     */
    public OrderResponse getOrderById(UUID orderId) {
        // 1. Lấy Entity từ Service (đã có sẵn hàm findById ném lỗi 404 nếu không thấy)
        Order order = orderService.findOrderById(orderId);

        // 2. Convert sang DTO
        // Hàm fromEntity() trong DTO chúng ta đã sửa ở bước trước
        // nên nó sẽ tự động map cả thông tin Payment (nếu có)
        return OrderResponse.fromEntity(order);
    }
}