package com.restaurant.restaurant_manager.config;

import com.restaurant.restaurant_manager.entity.Category;
import com.restaurant.restaurant_manager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategorySeeder {

    private final CategoryRepository categoryRepository;

    // Danh sách các danh mục bạn yêu cầu
    private static final List<String> CATEGORY_NAMES = Arrays.asList(
            "Khai Vị",
            "Món Chính",
            "Bánh và Tráng Miệng",
            "Đồ Uống",
            "Canh-Súp",
            "Cơm-Mì-Cháo"
    );

    @Transactional
    public void seed() {
        // Kiểm tra xem trong DB đã có dữ liệu chưa để tránh trùng lặp
        if (categoryRepository.count() == 0) {
            // Cách 1: Nếu bảng trống trơn thì thêm hết
            insertCategories();
        } else {
            // Cách 2: (Kỹ hơn) Duyệt từng cái, cái nào thiếu thì thêm
            // Dùng cách này an toàn hơn nếu sau này bạn bổ sung thêm danh mục mới vào code
            for (String name : CATEGORY_NAMES) {
                if (!categoryRepository.existsByName(name)) {
                    saveCategory(name);
                }
            }
        }
    }

    private void insertCategories() {
        for (String name : CATEGORY_NAMES) {
            saveCategory(name);
        }
    }

    private void saveCategory(String name) {
        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);
        System.out.println("✅ Đã khởi tạo Category: " + name);
    }
}