package com.restaurant.restaurant_manager.config.seeder;

import com.restaurant.restaurant_manager.entity.News;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.repository.NewsRepository;
import com.restaurant.restaurant_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NewsSeeder {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Transactional
    public void seed() {
        // 1. Tìm tài khoản Admin để gán làm tác giả bài viết
        Optional<User> adminOpt = userRepository.findByEmail("admin@gmail.com");

        // Nếu chưa có Admin thì không tạo tin tức được (tránh lỗi null author)
        if (adminOpt.isEmpty()) {
            System.out.println("⚠️ News Seeder: Không tìm thấy Admin để gán tác giả. Bỏ qua.");
            return;
        }

        User author = adminOpt.get();

        // 2. Tạo các bài viết mẫu
        createNewsIfNotExists(
                "Grand Opening - Tưng bừng khai trương giảm giá 20%",
                "Chào mừng quý khách đến với nhà hàng của chúng tôi. Nhân dịp khai trương, nhà hàng giảm giá 20% trên tổng hóa đơn cho tất cả khách hàng check-in tại quán. Chương trình áp dụng đến hết tháng này.",
                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=1000&auto=format&fit=crop", // Ảnh không gian nhà hàng
                author
        );

        createNewsIfNotExists(
                "Ra mắt thực đơn Mùa Hè: Hương vị biển cả",
                "Đầu bếp trưởng của chúng tôi vừa cho ra mắt bộ sưu tập món ăn mới lấy cảm hứng từ biển cả. Với các nguyên liệu tươi ngon như Tôm Hùm, Cua Hoàng Đế và Cá Hồi Na Uy, hứa hẹn sẽ mang lại trải nghiệm tuyệt vời.",
                "https://images.unsplash.com/photo-1559339352-11d035aa65de?q=80&w=1000&auto=format&fit=crop", // Ảnh món ăn hải sản
                author
        );

        createNewsIfNotExists(
                "Đêm nhạc Acoustic mỗi tối Thứ 6",
                "Vừa thưởng thức bít tết, vừa lắng nghe những giai điệu du dương. Đừng quên đặt bàn trước cho tối thứ 6 tuần này để có vị trí đẹp nhất nhé!",
                "https://images.unsplash.com/photo-1514525253440-b393452e3383?q=80&w=1000&auto=format&fit=crop", // Ảnh không gian chill/nhạc
                author
        );

        createNewsIfNotExists(
                "Thông báo nghỉ lễ Quốc Khánh 2/9",
                "Nhà hàng xin trân trọng thông báo lịch nghỉ lễ Quốc Khánh 2/9. Chúng tôi sẽ đóng cửa vào ngày 02/09 và mở cửa trở lại bình thường vào ngày 03/09. Kính chúc quý khách kỳ nghỉ lễ vui vẻ!",
                "https://images.unsplash.com/photo-1550966871-3ed3c47e2ce2?q=80&w=1000&auto=format&fit=crop", // Ảnh trang trọng/thông báo
                author
        );
    }

    private void createNewsIfNotExists(String title, String content, String imageUrl, User author) {
        if (!newsRepository.existsByTitle(title)) {
            News news = new News();
            news.setTitle(title);
            news.setContent(content);
            news.setImageUrl(imageUrl);
            news.setAuthor(author);

            newsRepository.save(news);
            System.out.println("✅ Đã tạo tin tức: " + title);
        }
    }
}