package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Order;
import org.springframework.data.domain.Pageable; // ✅ Import để giới hạn Top 5
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // ✅ Import để truyền tham số
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // --- CÁC HÀM CŨ CỦA BẠN (GIỮ NGUYÊN) ---

    // Tìm tất cả order của một khách hàng
    List<Order> findByCustomerId(UUID customerId);

    // Query tối ưu để fetch cả items tránh N+1 Query
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") UUID id);


    // --- CÁC HÀM MỚI: PHỤC VỤ THỐNG KÊ DASHBOARD ---

    // 1. Tính tổng doanh thu các đơn đã hoàn thành (COMPLETED) trong khoảng thời gian
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED' AND o.orderTime BETWEEN :start AND :end")
    Double sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 2. Đếm tổng số đơn hàng trong khoảng thời gian (Bao gồm cả đơn đang xử lý để biết traffic)
    long countByOrderTimeBetween(LocalDateTime start, LocalDateTime end);

    // 3. Thống kê doanh thu theo từng tháng trong một năm cụ thể
    // Trả về List các mảng Object: [Tháng, Tổng tiền]
    // Hàm FUNCTION('MONTH', ...) dùng để lấy tháng từ ngày trong DB (tương thích MySQL/PostgreSQL)
    @Query("SELECT FUNCTION('MONTH', o.orderTime) as month, SUM(o.totalAmount) as total " +
            "FROM Order o " +
            "WHERE o.status = 'COMPLETED' AND FUNCTION('YEAR', o.orderTime) = :year " +
            "GROUP BY FUNCTION('MONTH', o.orderTime) " +
            "ORDER BY month ASC")
    List<Object[]> getMonthlyRevenue(@Param("year") int year);

    // 4. Tìm Top sản phẩm bán chạy nhất
    // Join từ OrderItem -> Product để lấy tên
    // Trả về: [Tên món, Tổng số lượng bán, Tổng doanh thu từ món đó]
    @Query("SELECT p.name, SUM(oi.quantity), SUM(oi.quantity * oi.priceAtPurchase) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.product p " +
            "WHERE o.status = 'COMPLETED' " +
            "GROUP BY p.id, p.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getBestSellingProducts(Pageable pageable);
}