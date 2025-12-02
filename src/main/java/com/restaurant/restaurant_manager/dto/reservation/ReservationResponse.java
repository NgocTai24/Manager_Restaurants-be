package com.restaurant.restaurant_manager.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.restaurant_manager.entity.Reservation;
import com.restaurant.restaurant_manager.entity.enums.ReservationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReservationResponse {
    private UUID id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationTime;

    private int numberOfGuests;
    private ReservationStatus status;
    private String notes;

    // Thông tin khách
    private String customerName;
    private String customerPhone;

    // Thông tin bàn (nếu đã xếp)
    private String tableName;

    public static ReservationResponse fromEntity(Reservation entity) {
        return ReservationResponse.builder()
                .id(entity.getId())
                .reservationTime(entity.getReservationTime())
                .numberOfGuests(entity.getNumberOfGuests())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .customerName(entity.getCustomer().getName())
                .customerPhone(entity.getCustomer().getPhone())
                .tableName(entity.getTable() != null ? entity.getTable().getName() : "Chưa xếp bàn")
                .build();
    }
}