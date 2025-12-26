package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.entity.Order;
import com.restaurant.restaurant_manager.entity.Payment;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
import com.restaurant.restaurant_manager.entity.enums.PaymentMethod;
import com.restaurant.restaurant_manager.entity.enums.PaymentStatus;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.OrderRepository;
import com.restaurant.restaurant_manager.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PayOS payOS;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    @Transactional
    public Payment createPayment(Order order) {
        Payment payment = new Payment();
        payment.setOrder(order);
        // Lưu ý: Database vẫn lưu số tiền thật (150k) để sau này đối soát
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(order.getPaymentMethod());
        payment.setCreatedAt(LocalDateTime.now());

        if (order.getPaymentMethod() == PaymentMethod.COD) {
            payment.setStatus(PaymentStatus.UNPAID);
        } else {
            payment.setStatus(PaymentStatus.UNPAID);
            try {
                // Gọi hàm tạo link (bên trong hàm này sẽ fake tiền thành 2k)
                String checkoutUrl = createPayOSPaymentLink(order, payment);
                payment.setPaymentUrl(checkoutUrl);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("Failed to create PayOS payment: " + e.getMessage());
            }
        }

        return paymentRepository.save(payment);
    }

    private String createPayOSPaymentLink(Order order, Payment payment) throws Exception {
        // 1. Tạo orderCode
        long orderCode = System.currentTimeMillis() / 1000;
        payment.setPayosOrderCode(orderCode);

        String shortDescription = "DH" + orderCode;

        // ========================================================================
        // 🔴 CHẾ ĐỘ TEST: HARDCODE TIỀN VỀ 2.000 VNĐ (HOẶC 5.000 VNĐ)
        // ========================================================================

        // Bước A: Tạo 1 item giả thay thế cho list item thật
        // (Lý do: PayOS bắt buộc tổng tiền = tổng giá trị item, nên phải fake cả item)
        long finalAmount = 2000; // Số tiền bạn muốn chuyển (2k, 5k, 10k...)

        List<PaymentLinkItem> items = new ArrayList<>();
        items.add(PaymentLinkItem.builder()
                .name("Thanh toan test don hang") // Tên hiển thị trên PayOS
                .quantity(1)
                .price(finalAmount)
                .build());

        // Bước B: Tạo Request với số tiền giả
        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(finalAmount) // Chuyển đúng 2000đ
                .description(shortDescription)
                .items(items) // Gửi item giả đi
                .returnUrl(returnUrl + "?orderId=" + order.getId())
                .cancelUrl(cancelUrl + "?orderId=" + order.getId())
                .build();

        // ========================================================================
        // 🟢 KHI NÀO CHẠY THẬT (PRODUCTION) THÌ MỞ LẠI CODE DƯỚI NÀY, ĐÓNG ĐOẠN TRÊN
        /*
        List<PaymentLinkItem> items = order.getOrderItems().stream()
                .map(item -> PaymentLinkItem.builder()
                        .name(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtPurchase().longValue())
                        .build())
                .collect(Collectors.toList());

        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(order.getTotalAmount().longValue()) // Tiền thật
                .description(shortDescription)
                .items(items)
                .returnUrl(returnUrl + "?orderId=" + order.getId())
                .cancelUrl(cancelUrl + "?orderId=" + order.getId())
                .build();
        */
        // ========================================================================

        // 4. Gọi API
        CreatePaymentLinkResponse response = payOS.paymentRequests().create(request);

        return response.getCheckoutUrl();
    }

    // ... Các hàm confirmPayment, cancelPayment, getPaymentByOrderId giữ nguyên ...
    @Transactional
    public void confirmPayment(Long payosOrderCode, String transactionId) {
        Payment payment = paymentRepository.findByPayosOrderCode(payosOrderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        payment.setStatus(PaymentStatus.PAID);
        payment.setPayosTransactionId(transactionId);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus(com.restaurant.restaurant_manager.entity.enums.OrderStatus.PROCESSING);
        orderRepository.save(order);
    }

    @Transactional
    public void cancelPayment(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Cannot cancel a paid payment");
        }

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    public Payment getPaymentByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
    }

    @Transactional
    public Payment updatePaymentStatus(UUID paymentId, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        // Cập nhật trạng thái
        payment.setStatus(newStatus);

        // Logic bổ sung:
        // Nếu chuyển sang PAID (Đã thanh toán) -> Cập nhật thời gian thanh toán & Đổi trạng thái Order
        if (newStatus == PaymentStatus.PAID) {
            payment.setPaidAt(LocalDateTime.now());

            Order order = payment.getOrder();
            // Nếu đơn hàng đang PENDING (chờ thanh toán), chuyển sang PROCESSING (Đang xử lý/Làm món)
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.PROCESSING);
                orderRepository.save(order);
            }
        }
        // Nếu chuyển sang FAILED hoặc UNPAID -> Reset lại thời gian paidAt
        else {
            payment.setPaidAt(null);
        }

        return paymentRepository.save(payment);
    }
}