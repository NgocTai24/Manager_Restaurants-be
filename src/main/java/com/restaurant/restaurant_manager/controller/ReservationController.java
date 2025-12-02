package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.reservation.CreateReservationRequest;
import com.restaurant.restaurant_manager.dto.reservation.ReservationResponse;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.entity.Reservation;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.ReservationStatus;
import com.restaurant.restaurant_manager.service.ReservationFacade;
import com.restaurant.restaurant_manager.service.ReservationService;
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
public class ReservationController {

    private final ReservationFacade reservationFacade;
    private final ReservationService reservationService;

    // 1. Khách hàng (User) đặt bàn
    @PostMapping("/reservations")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReservationResponse>> bookTableUser(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateReservationRequest request) {
        return ApiResponse.created(reservationFacade.createReservationForUser(user, request), "Booking successful");
    }

    // 2. Khách vãng lai đặt bàn
    @PostMapping("/public/reservations")
    public ResponseEntity<ApiResponse<ReservationResponse>> bookTableGuest(
            @Valid @RequestBody CreateReservationRequest request) {
        return ApiResponse.created(reservationFacade.createReservationForGuest(request), "Booking successful");
    }

    // 3. Admin xem tất cả
    @GetMapping("/staff/reservations")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAll() {
        return ApiResponse.success(reservationService.getAllReservations(), "List reservations");
    }

    // 4. Staff xếp bàn (Confirm)
    @PutMapping("/staff/reservations/{id}/assign-table")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReservationResponse>> assignTable(
            @PathVariable UUID id,
            @RequestParam UUID tableId) {
        Reservation r = reservationService.assignTable(id, tableId);
        return ApiResponse.success(ReservationResponse.fromEntity(r), "Table assigned");
    }

    // 5. Staff cập nhật trạng thái (Hủy, Khách đến, Hoàn thành)
    @PutMapping("/staff/reservations/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestParam ReservationStatus status) {
        Reservation r = reservationService.updateStatus(id, status);
        return ApiResponse.success(ReservationResponse.fromEntity(r), "Status updated");
    }
}