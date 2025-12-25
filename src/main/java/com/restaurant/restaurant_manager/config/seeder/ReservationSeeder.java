package com.restaurant.restaurant_manager.config.seeder;

import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.entity.Reservation;
import com.restaurant.restaurant_manager.entity.RestaurantTable;
import com.restaurant.restaurant_manager.entity.enums.ReservationStatus;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import com.restaurant.restaurant_manager.repository.ReservationRepository;
import com.restaurant.restaurant_manager.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ReservationSeeder {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantTableRepository tableRepository;

    @Transactional
    public void seed() {
        if (reservationRepository.count() == 0) {
            List<Customer> customers = customerRepository.findAll();
            List<RestaurantTable> tables = tableRepository.findAll();

            if (customers.isEmpty() || tables.isEmpty()) {
                System.out.println("⚠️ ReservationSeeder: Cần có Customer và Table trước khi chạy.");
                return;
            }

            // --- 1. Dữ liệu QUÁ KHỨ (Đã hoàn thành) ---
            // Giúp có dữ liệu lịch sử
            createReservation(customers.get(0), tables.get(0), LocalDateTime.now().minusDays(2).withHour(18).withMinute(0), 4, ReservationStatus.COMPLETED, "Khách khen món ăn ngon");
            createReservation(customers.get(1), tables.get(1), LocalDateTime.now().minusDays(1).withHour(19).withMinute(30), 2, ReservationStatus.COMPLETED, null);
            createReservation(customers.get(2), null, LocalDateTime.now().minusDays(3).withHour(12).withMinute(0), 4, ReservationStatus.CANCELLED, "Khách bận đột xuất");

            // --- 2. Dữ liệu HÔM NAY (Quan trọng cho Dashboard) ---
            // Đơn CONFIRMED (Đã xếp bàn) - Sắp đến ăn
            createReservation(customers.get(0), tables.get(2), LocalDateTime.now().plusHours(2), 4, ReservationStatus.CONFIRMED, "Cần ghế trẻ em");

            // Đơn PENDING (Chưa xếp bàn) - Cần nhân viên xử lý ngay
            // Lấy customer khác nếu có, hoặc dùng lại customer cũ
            Customer guest = customers.size() > 3 ? customers.get(3) : customers.get(0);
            createReservation(guest, null, LocalDateTime.now().plusHours(4), 6, ReservationStatus.PENDING, "Tổ chức sinh nhật");

            // --- 3. Dữ liệu TƯƠNG LAI (Ngày mai, Tuần sau) ---
            createReservation(customers.get(1), null, LocalDateTime.now().plusDays(1).withHour(19).withMinute(0), 2, ReservationStatus.PENDING, "Ngồi gần cửa sổ");
            createReservation(customers.get(2), null, LocalDateTime.now().plusDays(5).withHour(11).withMinute(0), 10, ReservationStatus.PENDING, "Đặt tiệc công ty");

            System.out.println("✅ Reservation Seeder: Đã tạo dữ liệu đặt bàn mẫu.");
        }
    }

    private void createReservation(Customer customer, RestaurantTable table, LocalDateTime time, int guests, ReservationStatus status, String note) {
        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setReservationTime(time);
        // Giả sử ăn trong 2 tiếng
        reservation.setEndTime(time.plusHours(2));
        reservation.setNumberOfGuests(guests);
        reservation.setStatus(status);
        reservation.setNotes(note);
        reservation.setTable(table); // Có thể null nếu PENDING

        reservationRepository.save(reservation);
    }
}