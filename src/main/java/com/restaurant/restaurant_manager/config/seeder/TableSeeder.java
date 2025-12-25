package com.restaurant.restaurant_manager.config.seeder;

import com.restaurant.restaurant_manager.entity.RestaurantTable;
import com.restaurant.restaurant_manager.entity.enums.TableStatus;
import com.restaurant.restaurant_manager.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TableSeeder {

    private final RestaurantTableRepository tableRepository;

    @Transactional
    public void seed() {
        // Bỏ check count() == 0 đi, thay vào đó kiểm tra từng cái

        // 1. Khu vực Tầng 1: T1-01 -> T1-10
        for (int i = 1; i <= 10; i++) {
            String tableName = String.format("T1-%02d", i);
            createTableIfNotExists(tableName, 4, "Tầng 1 - Khu vực chung");
        }

        // 2. Khu vực Tầng 2: T2-01 -> T2-05
        for (int i = 1; i <= 5; i++) {
            String tableName = String.format("T2-%02d", i);
            createTableIfNotExists(tableName, 2, "Tầng 2 - Ban công view đẹp");
        }

        // 3. Khu vực VIP
        createTableIfNotExists("VIP-01", 10, "Phòng riêng máy lạnh, cách âm");
        createTableIfNotExists("VIP-02", 10, "Phòng riêng máy lạnh, cách âm");
    }

    private void createTableIfNotExists(String name, int capacity, String description) {
        // Kiểm tra từng bàn, chưa có mới tạo
        if (!tableRepository.existsByName(name)) {
            RestaurantTable table = new RestaurantTable();
            table.setName(name);
            table.setCapacity(capacity);
            table.setDescription(description);
            table.setStatus(TableStatus.AVAILABLE);

            tableRepository.save(table);
            System.out.println("✅ Đã tạo bàn: " + name);
        }
    }
}