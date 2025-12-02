package com.restaurant.restaurant_manager.entity.enums;

public enum ReservationStatus {
    PENDING,    // Mới đặt, chờ xác nhận
    CONFIRMED,  // Đã xác nhận (đã xếp bàn)
    ARRIVED,    // Khách đã đến (Check-in)
    COMPLETED,  // Đã ăn xong (Check-out)
    CANCELLED,  // Hủy
    NO_SHOW     // Khách không đến
}