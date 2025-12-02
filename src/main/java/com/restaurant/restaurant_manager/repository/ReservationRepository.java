package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Reservation;
import com.restaurant.restaurant_manager.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByCustomerId(UUID customerId);

    // Tìm các đơn đặt bàn có khả năng xung đột thời gian với 1 khung giờ [start, end]
    // Status phải khác CANCELLED, COMPLETED, NO_SHOW
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.status IN ('PENDING', 'CONFIRMED', 'ARRIVED') " +
            "AND (" +
            "   (r.reservationTime BETWEEN :start AND :end) OR " +
            "   (r.endTime BETWEEN :start AND :end) OR " +
            "   (:start BETWEEN r.reservationTime AND r.endTime)" +
            ")")
    List<Reservation> findConflictingReservations(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);
}