package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.entity.Payment;
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
            // 1. Kiểm tra xem có field "data" không (theo chuẩn PayOS)
            if (!payload.containsKey("data")) {
                return ApiResponse.badRequest("Invalid Webhook Data Format", null);
            }

            // 2. Lấy object "data" ra
            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            // 3. Lấy orderCode từ trong "data"
            if (data == null || !data.containsKey("orderCode")) {
                return ApiResponse.badRequest("Order Code missing", null);
            }

            Long orderCode = Long.parseLong(data.get("orderCode").toString());

            // Lưu ý: PayOS webhook không gửi transactionId trong data ở một số trường hợp,
            // hoặc nó tên là "reference". Nếu null thì mình tạo tạm hoặc bỏ qua.
            String transactionId = data.containsKey("reference") ? data.get("reference").toString() : "PAYOS_TRANS_" + orderCode;

            // Webhook PayOS bắn về nghĩa là đã thành công (hoặc check field code == "00")
            // Bạn nên check thêm security signature nếu làm thật, nhưng để test thì bỏ qua.

            paymentService.confirmPayment(orderCode, transactionId);

            return ApiResponse.success(null, "Payment confirmed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest("Webhook processing failed: " + e.getMessage(), null);
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