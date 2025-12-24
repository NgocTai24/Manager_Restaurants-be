package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.response.StatisticResponse;
import com.restaurant.restaurant_manager.entity.enums.ReservationStatus;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import com.restaurant.restaurant_manager.repository.OrderRepository;
import com.restaurant.restaurant_manager.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;

    public StatisticResponse getDashboardStats() {
        LocalDateTime startToday = LocalDate.now().atStartOfDay();
        LocalDateTime endToday = LocalDate.now().atTime(LocalTime.MAX);
        int currentYear = LocalDate.now().getYear();

        // 1. Doanh thu hôm nay (Nếu null thì trả về 0.0)
        Double revenueToday = orderRepository.sumRevenueBetween(startToday, endToday);
        if (revenueToday == null) revenueToday = 0.0;

        // 2. Số đơn hôm nay
        long ordersToday = orderRepository.countByOrderTimeBetween(startToday, endToday);

        // 3. Đơn đặt bàn đang chờ hoặc đã xác nhận (chưa ăn xong)
        long pendingReservations = reservationRepository.countByStatusIn(
                Arrays.asList(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );

        // 4. Tổng khách hàng
        long totalCustomers = customerRepository.count();

        // 5. Biểu đồ doanh thu 12 tháng
        List<Object[]> monthlyData = orderRepository.getMonthlyRevenue(currentYear);
        List<StatisticResponse.MonthlyRevenue> monthlyChart = new ArrayList<>();
        // Khởi tạo mảng đủ 12 tháng (mặc định 0) để biểu đồ không bị gãy khúc
        for (int i = 1; i <= 12; i++) {
            monthlyChart.add(StatisticResponse.MonthlyRevenue.builder().month(i).revenue(0.0).build());
        }
        // Map dữ liệu DB vào list
        for (Object[] row : monthlyData) {
            int month = (int) row[0];
            Double amount = (Double) row[1];
            monthlyChart.get(month - 1).setRevenue(amount);
        }

        // 6. Top 5 sản phẩm bán chạy
        List<Object[]> bestSellersData = orderRepository.getBestSellingProducts(PageRequest.of(0, 5));
        List<StatisticResponse.BestSellingProduct> topProducts = new ArrayList<>();
        for (Object[] row : bestSellersData) {
            topProducts.add(StatisticResponse.BestSellingProduct.builder()
                    .productName((String) row[0])
                    .quantitySold((Long) row[1])
                    .totalRevenue((Double) row[2])
                    .build());
        }

        return StatisticResponse.builder()
                .revenueToday(revenueToday)
                .ordersToday(ordersToday)
                .pendingReservations(pendingReservations)
                .totalCustomers(totalCustomers)
                .monthlyRevenue(monthlyChart)
                .bestSellingProducts(topProducts)
                .build();
    }
}