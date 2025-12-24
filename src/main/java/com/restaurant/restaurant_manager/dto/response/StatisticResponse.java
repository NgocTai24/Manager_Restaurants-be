package com.restaurant.restaurant_manager.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatisticResponse {
    // 1. Số liệu tổng quan hôm nay
    private Double revenueToday;
    private long ordersToday;
    private long pendingReservations;
    private long totalCustomers;

    // 2. Dữ liệu biểu đồ doanh thu theo tháng (cho năm hiện tại)
    private List<MonthlyRevenue> monthlyRevenue;

    // 3. Top món ăn bán chạy
    private List<BestSellingProduct> bestSellingProducts;

    // --- Inner Classes cho cấu trúc con ---
    @Data
    @Builder
    public static class MonthlyRevenue {
        private int month;
        private Double revenue;
    }

    @Data
    @Builder
    public static class BestSellingProduct {
        private String productName;
        private Long quantitySold;
        private Double totalRevenue;
    }
}