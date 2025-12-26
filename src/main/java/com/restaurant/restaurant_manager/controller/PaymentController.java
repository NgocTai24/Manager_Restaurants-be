package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.entity.Payment;
import com.restaurant.restaurant_manager.entity.enums.PaymentStatus;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Webhook từ PayOS (khi thanh toán thành công)
     * POST /api/v1/payments/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<Object>> payosWebhook(@RequestBody Map<String, Object> payload) {
        // 1. Log xem PayOS gửi gì đến (Quan trọng để debug)
        System.out.println("🔔 WEBHOOK RECEIVED: " + payload);

        try {
            // Kiểm tra dữ liệu đầu vào
            if (payload == null || !payload.containsKey("data")) {
                return ApiResponse.success(null, "No data received");
            }

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            if (data == null || !data.containsKey("orderCode")) {
                return ApiResponse.success(null, "Order Code missing");
            }

            // 2. Parse dữ liệu an toàn (Tránh lỗi NumberFormat khi PayOS gửi dữ liệu rác)
            long orderCode = -1;
            try {
                orderCode = Long.parseLong(data.get("orderCode").toString());
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Lỗi format orderCode (Có thể là data test): " + e.getMessage());
                return ApiResponse.success(null, "Invalid order code");
            }

            String transactionId = data.containsKey("reference") ? data.get("reference").toString() : "UNKNOWN";

            // 3. GỌI SERVICE CẬP NHẬT DB
            try {
                paymentService.confirmPayment(orderCode, transactionId);
                System.out.println("✅ Database updated for OrderCode: " + orderCode);

                // 4. 🔥 CHỈ BẮN SOCKET KHI CẬP NHẬT DB THÀNH CÔNG 🔥
                String message = "Đơn hàng " + orderCode + " thanh toán thành công!";

                // Gửi vào topic chung "/topic/payments"
                messagingTemplate.convertAndSend("/topic/payments", message);
                System.out.println("🚀 Socket sent: " + message);

            } catch (ResourceNotFoundException e) {
                // Đây là trường hợp PayOS gửi mã Test (ví dụ 123) mà DB không có -> Bỏ qua không báo lỗi
                System.out.println("⚠️ Webhook Test (Order not found): " + orderCode + " - Ignored.");
            }

            return ApiResponse.success(null, "Webhook processed");

        } catch (Exception e) {
            e.printStackTrace();
            // Vẫn trả về 200 OK để PayOS không gửi lại, nhưng in lỗi ra console
            return ApiResponse.success(null, "Webhook processing failed: " + e.getMessage());
        }
    }

    /**
     * Lấy thông tin thanh toán của order
     * GET /api/v1/payments/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentByOrderId(@PathVariable UUID orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ApiResponse.success(payment, "Payment retrieved");
    }

    @PutMapping("/{paymentId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Payment>> updatePaymentStatus(
            @PathVariable UUID paymentId,
            @RequestParam PaymentStatus status
    ) {
        Payment updatedPayment = paymentService.updatePaymentStatus(paymentId, status);
        return ApiResponse.success(updatedPayment, "Payment status updated successfully");
    }
}