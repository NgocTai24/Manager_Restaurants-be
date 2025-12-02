package com.restaurant.restaurant_manager.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.restaurant_manager.dto.order.CreateOrderRequest;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateReservationRequest {

    // Thông tin khách (Nếu là Guest đặt)
    private CreateOrderRequest.CustomerInfo customerInfo;

    @NotNull(message = "Reservation time is required")
    @Future(message = "Thời gian đặt chỗ phải ở trong tương lai")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationTime;

    @Min(value = 1, message = "Number of guests must be at least 1")
    private int numberOfGuests;

    private String notes;
}