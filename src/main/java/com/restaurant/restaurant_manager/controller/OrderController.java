package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.order.CreateOrderRequest;
import com.restaurant.restaurant_manager.dto.order.OrderResponse;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
import com.restaurant.restaurant_manager.service.OrderFacade;
import com.restaurant.restaurant_manager.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;
    private final OrderService orderService;

    /**
     * API 1: Khách hàng tự đặt món (Phải đăng nhập)
     * POST /api/v1/orders
     */
    @PostMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrderForUser(
            @AuthenticationPrincipal User currentUser, // Lấy user từ token
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderFacade.placeOrderForUser(currentUser, request);
        return ApiResponse.created(response, "Order placed successfully");
    }

    /**
     * API 2: Khách vãng lai / Staff đặt giúp (Không cần đăng nhập hoặc Staff dùng)
     * POST /api/v1/public/orders
     */
    @PostMapping("/public/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrderForGuest(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        // Lưu ý: Request body phải có field `customerInfo`
        OrderResponse response = orderFacade.placeOrderForGuest(request);
        return ApiResponse.created(response, "Guest order placed successfully");
    }

    /**
     * API 3: Staff tạo đơn (Giống guest, nhưng có quyền Staff)
     * POST /api/v1/staff/orders
     */
    @PostMapping("/staff/orders")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrderForStaff(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderFacade.placeOrderForGuest(request);
        return ApiResponse.created(response, "Order created by staff successfully");
    }

    /**
     * ADMIN: Xem tất cả đơn hàng
     */
    @GetMapping("/staff/orders")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        return ApiResponse.success(orderService.getAllOrders(), "List of orders");
    }

    /**
     * Lấy chi tiết đơn hàng
     * GET /api/v1/staff/orders/{id}
     */
    @GetMapping("/staff/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Chỉ Admin hoặc Staff mới xem được
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID id) {

        OrderResponse response = orderFacade.getOrderById(id);

        return ApiResponse.success(response, "Order details retrieved successfully");
    }

    /**
     * API 5: ADMIN Cập nhật trạng thái đơn hàng
     * PUT /api/v1/admin/orders/{id}/status?status=CONFIRMED
     */
    @PutMapping("/staff/orders/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam("status") OrderStatus status
    ) {
        // Gọi qua Facade để vừa update DB vừa gửi mail
        OrderResponse updatedOrder = orderFacade.updateOrderStatus(id, status);
        return ApiResponse.success(updatedOrder, "Order status updated successfully");
    }

    /**
     * User tự hủy đơn của mình (trong vòng 10p, trạng thái PENDING)
     * PUT /api/v1/orders/{id}/cancel
     */
    @PutMapping("/orders/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelMyOrder(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser
    ) {
        OrderResponse response = orderFacade.cancelOrderForUser(currentUser, id);
        return ApiResponse.success(response, "Order cancelled successfully");
    }

    // --- API MỚI: STAFF HỦY ĐƠN ---
    /**
     * Staff hủy đơn (quyền mạnh hơn)
     * PUT /api/v1/staff/orders/{id}/cancel
     */
    @PutMapping("/staff/orders/{id}/cancel")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrderStaff(
            @PathVariable UUID id
    ) {
        OrderResponse response = orderFacade.cancelOrderForStaff(id);
        return ApiResponse.success(response, "Order cancelled by staff");
    }
}