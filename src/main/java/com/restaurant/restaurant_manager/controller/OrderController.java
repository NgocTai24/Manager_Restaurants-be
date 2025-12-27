package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.order.CreateOrderRequest;
import com.restaurant.restaurant_manager.dto.order.OrderResponse;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.dto.response.PageResponse;
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


    @PostMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrderForUser(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderFacade.placeOrderForUser(currentUser, request);
        return ApiResponse.created(response, "Order placed successfully");
    }


    @PostMapping("/public/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrderForGuest(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        // Lưu ý: Request body phải có field `customerInfo`
        OrderResponse response = orderFacade.placeOrderForGuest(request);
        return ApiResponse.created(response, "Guest order placed successfully");
    }

    @PostMapping("/staff/orders")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrderForStaff(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderFacade.placeOrderForGuest(request);
        return ApiResponse.created(response, "Order created by staff successfully");
    }

    @GetMapping("/staff/orders")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<OrderResponse> orders = orderService.getAllOrders(page, size);
        return ApiResponse.success(orders, "List of orders retrieved successfully");
    }


    @GetMapping("/staff/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID id) {

        OrderResponse response = orderFacade.getOrderById(id);

        return ApiResponse.success(response, "Order details retrieved successfully");
    }


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


    @PutMapping("/orders/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelMyOrder(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser
    ) {
        OrderResponse response = orderFacade.cancelOrderForUser(currentUser, id);
        return ApiResponse.success(response, "Order cancelled successfully");
    }


    @PutMapping("/staff/orders/{id}/cancel")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrderStaff(
            @PathVariable UUID id
    ) {
        OrderResponse response = orderFacade.cancelOrderForStaff(id);
        return ApiResponse.success(response, "Order cancelled by staff");
    }
}