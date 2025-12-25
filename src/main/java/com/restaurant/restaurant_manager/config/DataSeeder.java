package com.restaurant.restaurant_manager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    // Inject các Seeder con
    private final UserSeeder userSeeder;
    private final CustomerSeeder customerSeeder;
    private final CategorySeeder categorySeeder;
    private final ProductSeeder productSeeder;
    private final TableSeeder tableSeeder;
    private final NewsSeeder newsSeeder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🚀 Bắt đầu khởi tạo dữ liệu mẫu...");

        userSeeder.seed();
        customerSeeder.seed();

        categorySeeder.seed();
        productSeeder.seed();
        tableSeeder.seed();
        newsSeeder.seed();

        System.out.println("🏁 Hoàn tất khởi tạo dữ liệu.");
    }
}