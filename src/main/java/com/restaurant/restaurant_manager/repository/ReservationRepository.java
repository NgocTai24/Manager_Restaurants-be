package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Reservation;
import com.restaurant.restaurant_manager.entity.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    // --- 1. Tìm theo trạng thái (Có phân trang) ---
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

    // --- 2. Tìm trong khoảng thời gian (Có phân trang) ---
    Page<Reservation> findByReservationTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // --- 3. Lịch sử đặt bàn của khách ---
    List<Reservation> findByCustomerId(UUID customerId);

    // --- 4. Check trùng lịch ---
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.status IN ('PENDING', 'CONFIRMED', 'ARRIVED') " +
            "AND (" +
            "   (r.reservationTime BETWEEN :start AND :end) OR " +
            "   (r.endTime BETWEEN :start AND :end) OR " +
            "   (:start BETWEEN r.reservationTime AND r.endTime)" +
            ")")
    List<Reservation> findConflictingReservations(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    // --- 5. MỚI THÊM: PHỤC VỤ THỐNG KÊ (DASHBOARD) ---
    // Đếm số lượng đơn theo một danh sách các trạng thái
    // Ví dụ: Đếm xem có bao nhiêu đơn đang PENDING hoặc CONFIRMED (để hiện lên Dashboard)
    long countByStatusIn(List<ReservationStatus> statuses);
}