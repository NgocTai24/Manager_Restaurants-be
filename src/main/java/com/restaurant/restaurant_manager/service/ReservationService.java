package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.reservation.CreateReservationRequest;
import com.restaurant.restaurant_manager.dto.reservation.ReservationResponse;
import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.entity.Reservation;
import com.restaurant.restaurant_manager.entity.RestaurantTable;
import com.restaurant.restaurant_manager.entity.enums.ReservationStatus;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.ReservationRepository;
import com.restaurant.restaurant_manager.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;

    // Mặc định mỗi slot ăn là 2 tiếng
    private static final int DINING_DURATION_HOURS = 2;

    @Transactional
    public Reservation createReservation(Customer customer, CreateReservationRequest request) {
        LocalDateTime checkIn = request.getReservationTime();
        LocalDateTime checkOut = checkIn.plusHours(DINING_DURATION_HOURS);

        // 1. Tạo Reservation (Chưa gán bàn vội, để PENDING cho Staff xếp sau)
        // Hoặc: Nếu muốn auto xếp bàn thì viết logic tìm bàn trống ở đây.
        // Ở đây mình làm theo hướng: Khách đặt -> Hệ thống ghi nhận -> Staff xếp bàn.

        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setReservationTime(checkIn);
        reservation.setEndTime(checkOut);
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setNotes(request.getNotes());
        reservation.setStatus(ReservationStatus.PENDING);

        return reservationRepository.save(reservation);
    }

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // --- LOGIC XẾP BÀN (Dành cho Staff) ---
    @Transactional
    public Reservation assignTable(UUID reservationId, UUID tableId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        // Check 1: Sức chứa của bàn
        if (table.getCapacity() < reservation.getNumberOfGuests()) {
            throw new BadRequestException("Table capacity is not enough");
        }

        // Check 2: Bàn có đang bị trùng lịch không?
        boolean isConflict = isTableBusy(tableId, reservation.getReservationTime(), reservation.getEndTime());
        if (isConflict) {
            throw new BadRequestException("Table is already booked in this time slot");
        }

        reservation.setTable(table);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        return reservationRepository.save(reservation);
    }

    // Helper check trùng lịch
    private boolean isTableBusy(UUID tableId, LocalDateTime start, LocalDateTime end) {
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(start, end);
        // Lọc xem trong các đơn trùng giờ, có đơn nào đã book cái bàn này chưa
        return conflicts.stream()
                .anyMatch(r -> r.getTable() != null && r.getTable().getId().equals(tableId));
    }

    @Transactional
    public Reservation updateStatus(UUID id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }
}