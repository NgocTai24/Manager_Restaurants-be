package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.order.CreateOrderRequest;
import com.restaurant.restaurant_manager.dto.order.OrderResponse;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
import com.restaurant.restaurant_manager.service.OrderFacade;
import com.restaurant.restaurant_manager.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;
    private final OrderService orderService;

    // ==================== PUBLIC APIs ====================

    /**
     * API cho phép khách (Guest) tạo đơn hàng
     * POST /api/v1/public/orders
     */
    @PostMapping("/public/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse orderResponse = orderFacade.createPublicOrder(request);
        return ApiResponse.created(orderResponse, "Order created successfully");
    }

    // ==================== ADMIN APIs ====================

    /**
     * API cho Admin xem tất cả đơn hàng
     * GET /api/v1/admin/orders
     */
    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ApiResponse.success(orders, "Orders retrieved successfully");
    }

    /**
     * API cho Admin cập nhật trạng thái đơn hàng
     * PUT /api/v1/admin/orders/{id}/status
     */
    @PutMapping("/admin/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam("status") OrderStatus status
    ) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, status);
        return ApiResponse.success(updatedOrder, "Order status updated");
    }
}