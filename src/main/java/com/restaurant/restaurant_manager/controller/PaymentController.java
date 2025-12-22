package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.entity.Payment;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Webhook từ PayOS (khi thanh toán thành công)
     * POST /api/v1/payments/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<Object>> payosWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Kiểm tra data cơ bản
            if (payload == null || !payload.containsKey("data")) {
                return ApiResponse.success(null, "Webhook received (No data)");
            }

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            if (data == null || !data.containsKey("orderCode")) {
                return ApiResponse.success(null, "Order Code missing");
            }

            // 2. Parse dữ liệu
            long orderCode = -1;
            try {
                orderCode = Long.parseLong(data.get("orderCode").toString());
            } catch (NumberFormatException e) {
                // Nếu PayOS gửi chữ "TEST" hoặc số lỗi
                return ApiResponse.success(null, "Invalid order code format");
            }

            String transactionId = data.containsKey("reference") ? data.get("reference").toString() : "UNKNOWN";

            // 3. GỌI SERVICE (CÓ BẮT LỖI NOT FOUND)
            try {
                paymentService.confirmPayment(orderCode, transactionId);
                System.out.println("✅ Payment confirmed: " + orderCode);
            } catch (ResourceNotFoundException e) {
                // ⚠️ QUAN TRỌNG: Đây là chỗ sửa lỗi
                // Nếu không tìm thấy đơn (do PayOS test), in log ra nhưng VẪN TRẢ VỀ SUCCESS
                System.out.println("⚠️ Webhook Test (Không tìm thấy đơn): " + orderCode + " - Bỏ qua để PayOS không báo lỗi.");
            }

            // Luôn trả về 200 OK
            return ApiResponse.success(null, "Webhook processed");

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.success(null, "Webhook error handled: " + e.getMessage());
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
}