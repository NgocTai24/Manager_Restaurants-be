package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.reservation.CreateReservationRequest;
import com.restaurant.restaurant_manager.dto.reservation.ReservationResponse;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.dto.response.PageResponse;
import com.restaurant.restaurant_manager.entity.Reservation;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.entity.enums.ReservationStatus;
import com.restaurant.restaurant_manager.service.ReservationFacade;
import com.restaurant.restaurant_manager.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ReservationResponse> result = reservationService.getAllReservations(page, size);
        return ApiResponse.success(result, "List reservations retrieved successfully");
    }

    // 3.1. Tìm kiếm theo trạng thái (Ví dụ: Lấy các đơn PENDING để duyệt)
    // GET /api/v1/staff/reservations/status?status=PENDING&page=0
    @GetMapping("/staff/reservations/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> getByStatus(
            @RequestParam ReservationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ReservationResponse> result = reservationService.getReservationsByStatus(status, page, size);
        return ApiResponse.success(result, "Reservations by status retrieved");
    }

    // 3.2. Tìm kiếm theo ngày (Ví dụ: Xem lịch hôm nay 2023-12-25)
    // GET /api/v1/staff/reservations/date?date=2023-12-25
    @GetMapping("/staff/reservations/date")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ReservationResponse> result = reservationService.getReservationsByDate(date, page, size);
        return ApiResponse.success(result, "Reservations by date retrieved");
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