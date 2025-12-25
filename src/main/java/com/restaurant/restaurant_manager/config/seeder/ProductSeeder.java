package com.restaurant.restaurant_manager.config.seeder;

import com.restaurant.restaurant_manager.entity.Category;
import com.restaurant.restaurant_manager.entity.Product;
import com.restaurant.restaurant_manager.repository.CategoryRepository;
import com.restaurant.restaurant_manager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductSeeder {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void seed() {
        // --- 1. Danh mục: Khai Vị ---
        seedProductsForCategory("Khai Vị", new ProductData[]{
                new ProductData("Salad rau sốt cam", 40000.0, "Salad rau củ tươi, giòn mát, kết hợp sốt cam chua ngọt nhẹ nhàng, mở đầu bữa ăn hoàn hảo.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/untitled1-1.jpg?v=1667882668260"),
                new ProductData("Phở cuốn", 70000.0, "Phở cuốn nhân thịt bò thơm ngon, rau sống tươi mát, chấm nước mắm pha đặc trưng.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/untitled1f119f567b16045a78f61d.jpg?v=1667882617523"),
                new ProductData("Salad rau sốt bơ đậu phộng", 40000.0, "Salad rau xanh tươi, sốt bơ đậu phộng béo ngậy, kết hợp khoai tây chiên giòn rụm.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/untitled1bb4fdbb3bd7845448a799-a1c5a559-3505-435f-9278-d7ba29e9c529.jpg?v=1667882632337"),
                new ProductData("Nem lụi nướng mía", 78000.0, "Nem lụi nướng trên que mía, thịt ướp đậm đà, vỏ giòn, hương thơm nướng mía đặc trưng.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/11f732523eccd493dab32cd1c47283.jpg?v=1667882302237"),
                new ProductData("Bánh mì bơ tỏi", 55000.0, "Bánh mì nướng vàng giòn, quết bơ tỏi thơm lừng, món ăn đơn giản nhưng cực kỳ hấp dẫn.", "https://daylambanh.edu.vn/wp-content/uploads/2017/03/banh-mi-bo-toi.jpg")
        });

        // --- 2. Danh mục: Món Chính ---
        seedProductsForCategory("Món Chính", new ProductData[]{
                new ProductData("Gỏi tai heo hoa chuối", 100000.0, "Gỏi tai heo giòn sần sật kết hợp hoa chuối tươi, hòa cùng nước mắm chua ngọt và đậu phộng rang, món khai vị độc đáo nhưng cũng có thể dùng chính.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/untitled114148eed72724d16a9d2c.jpg?v=1667882605937"),
                new ProductData("Gà cuốn lá dứa", 135000.0, "Gà tươi cuốn trong lá dứa, áp chảo hoặc nướng, giữ trọn hương thơm tự nhiên, mềm ngọt và thơm lừng.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/2-2.jpg?v=1667882572730"),
                new ProductData("Ức gà đút lò phủ lá chanh", 148000.0, "Ức gà nướng trong lò, phủ lá chanh thơm phức, thịt gà mềm, đậm vị và thơm mùi thảo mộc.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1-2.jpg?v=1667882535080"),
                new ProductData("Ba rọi nướng riềng mẻ", 120000.0, "Ba rọi heo ướp riềng mẻ, nướng vàng giòn lớp bì, thịt mềm, đậm đà hương vị truyền thống Việt.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1240f05c5ee174bcdaf47d5ec33197.jpg?v=1667882506833"),
                new ProductData("Ba rọi chiên mắm ngò", 130000.0, "Ba rọi heo chiên giòn, tẩm mắm ngò thơm lừng, lớp thịt mềm, bì giòn tan, hương vị đậm đà khó quên.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/17ad3f36d9db047aa93f83dc10abc6.jpg?v=1667882482780"),
                new ProductData("Sụn gà xóc muối Tây Ninh", 120000.0, "Sụn gà giòn rụm, xóc muối Tây Ninh cay thơm, ăn nhâm nhi cực đã, món ăn vặt nhưng cũng là món chính ngon miệng.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1361d484343ae4cd79a7567bf7c85a.jpg?v=1667882319867")
        });

        // --- 3. Danh mục: Bánh và Tráng Miệng ---
        seedProductsForCategory("Bánh và Tráng Miệng", new ProductData[]{
                new ProductData("Bánh flan", 25000.0, "Bánh flan mềm mịn, béo nhẹ và thơm vị caramel.", "https://imgs.search.brave.com/EfPZCUV3BKMIP0UBupHGvXsVxJq1Pv2z7kuBawuTF2c/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9zaW1l/eGNvZGwuY29tLnZu/L3dwLWNvbnRlbnQv/dXBsb2Fkcy8yMDI0/LzA1L2NhY2gtbGFt/LXNvdC1jYS1waGUt/YW4tYmFuaC1mbGFu/LTMuanBn"),
                new ProductData("Bánh su kem", 30000.0, "Vỏ bánh mềm, nhân kem béo ngậy tan ngay khi thưởng thức.", "https://imgs.search.brave.com/4kpxFTHRuys2Jn5EuDlIPo257vae8i51IpEheQi27Ao/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/aHVvbmduZ2hpZXBh/YXUuY29tL3dwLWNv/bnRlbnQvdXBsb2Fk/cy8yMDE5LzA4L2Jh/bmgtc3Uta2VtLW5n/b24tbmdhdC1uZ2F5/LmpwZw"),
                new ProductData("Rau câu sữa dừa hoa mộc", 25000.0, "Rau câu mát lạnh, thơm nhẹ hoa mộc và béo dịu vị sữa dừa.", "https://imgs.search.brave.com/LvzEpv9R0PUGWxgRRuWvjkCd_EeIulxnVyr3gFe0l4o/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9ibG9n/YW5jaG9pLmNvbS93/cC1jb250ZW50L3Vw/bG9hZHMvMjAyNC8w/Ni9jYWNoLWxhbS10/aGFjaC1yYXUtY2F1/LW5nb24tbmhhdC0x/OC02OTZ4NDc0Lmpw/Zw"),
                new ProductData("Kem vani", 30000.0, "Kem vani ngọt nhẹ, thơm dịu, mát lạnh sảng khoái.", "https://imgs.search.brave.com/MXKjW52UzY9Pen5MRW823oqS5KK_zfwIcLID8JszJgk/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvMTMy/NjE0Mzk2OS92aS9h/bmgvYiVDMyVBMXQt/diVFMSVCQiU5Qmkt/bmglRTElQkIlQUZu/Zy1xdSVFMSVCQSVB/My1iJUMzJUIzbmct/a2VtLXZhbmkuanBn/P3M9NjEyeDYxMiZ3/PTAmaz0yMCZjPUpD/TVdSVTlrLTJUT2tT/Yy0xa3lDX0owcUQw/eUp6RnRWc08wX3ZM/anR2VkU9"),
                new ProductData("Bánh tiramisu", 45000.0, "Tiramisu mềm ẩm, vị cà phê và cacao hòa quyện tinh tế.", "https://imgs.search.brave.com/ILJrJvLYo2DkWoAiiXxC2hkascGaaBdE9TE47elg9l8/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/dGdkZC52bi9GaWxl/cy8yMDIxLzA4LzA4/LzEzNzM5MDgvdGly/YW1pc3UtbGEtZ2kt/eS1uZ2hpYS1jdWEt/YmFuaC10aXJhbWlz/dS0yMDIxMDgwODIz/MDQyNzE4MDYuanBn"),
                new ProductData("Bánh chuối hấp", 48000.0, "Bánh chuối hấp dẻo thơm, vị ngọt tự nhiên, ăn kèm nước cốt dừa béo nhẹ.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1b9f1d3769f3640b1a2b386fb6b453.jpg?v=1667881682663")
        });

        // --- 4. Danh mục: Đồ Uống ---
        seedProductsForCategory("Đồ Uống", new ProductData[]{
                new ProductData("Dương chi cam lộ", 55000.0, "Thức uống tráng miệng mát lạnh với xoài chín, bưởi tươi và nước cốt dừa béo nhẹ, vị ngọt thanh dễ uống", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1c8da310231574e189b9012e3125a3.jpg?v=1667881665957"),
                new ProductData("Trà lài nhãn", 48000.0, "Trà lài thơm dịu kết hợp cùng nhãn tươi ngọt mát, mang lại cảm giác thanh nhẹ và thư giãn", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/19fe207c1918443c493a8ffc37de05.jpg?v=1667881644533"),
                new ProductData("Trà sữa Oolong", 50000.0, "Trà Oolong đậm hương kết hợp sữa béo vừa phải, vị trà hậu ngọt, không gắt", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1f8b8eb2049ed4362bd32f0899192c.jpg?v=1667881453383"),
                new ProductData("Nước ép detox", 45000.0, "Nước ép rau củ và trái cây tươi giúp thanh lọc cơ thể, ít ngọt, giàu vitamin", "https://imgs.search.brave.com/ZB_OKhQwbT3WtdA9msqpmE5mx9Qshv7DwtBGPnOnivM/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9hcGkt/b21uaS5tdXRvc2ku/Y29tL3YwL3MzL3Zp/ZXcvdGhhbmgtbG9j/LXZvaS1udW9jLWRl/dG94LWJhbmctcmF1/LWN1LmpwZw"),
                new ProductData("Sinh tố bơ", 55000.0, "Sinh tố bơ xay nhuyễn cùng sữa tươi, béo mịn, thơm ngon và giàu dinh dưỡng", "https://imgs.search.brave.com/EqJDRJU0K6qCNv5RGruTg9Bk0b43jL8WR1wBYg3fykI/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9oYy5j/b20udm4vaS9lY29t/bWVyY2UvbWVkaWEv/Y2szMTk5MTgwLXRo/dW9uZy10aHVjLXNp/bmgtdG8tYm8tZHVh/LW1hdC1sYW5oLmpw/Zw")
        });

        // --- 5. Danh mục: Canh-Súp ---
        seedProductsForCategory("Canh-Súp", new ProductData[]{
                new ProductData("Canh mướp hương nhồi thịt nấm", 65000.0, "Mướp hương mềm ngọt được nhồi thịt và nấm, nấu thanh mát.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/untitled19295a1b9ecd041238d6bd.jpg?v=1667882495937"),
                new ProductData("Canh gà nấm", 75000.0, "Thịt gà tươi hầm cùng nấm tạo vị ngọt tự nhiên, bổ dưỡng.", "https://imgs.search.brave.com/plZuIqVI1k_ETZrPv7ijncXcgk2LR6ALRRZIp96OM1w/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9maWxl/LmhzdGF0aWMubmV0/LzIwMDAwMDM4NTcx/Ny9hcnRpY2xlL2Jp/YV9lZWE2YjhhZTFh/MDc0NWRmOTRkZTA5/MDE5NWYxYzI5NC5q/cGc"),
                new ProductData("Súp tôm bí đỏ", 60000.0, "Súp bí đỏ béo mịn kết hợp tôm tươi, thơm ngon và giàu dinh dưỡng.", "https://imgs.search.brave.com/MdW7aQrq_giQlJB1bEgwQaa6sm3nlxBIbtpg0NatkAs/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9jZG4u/ZXZhLnZuL3VwbG9h/ZC8zLTIwMjMvaW1h/Z2VzLzIwMjMtMDct/MTkvMTAtY2FjaC1u/YXUtc3VwLWJpLWRv/LW5nb24tY2h1YW4t/dmktbmd1b2ktbG9u/LWhheS10cmUtY29u/LWRldS1tZS10aXQt/MTktMTY4OTc1NDIx/NC0yNTgtd2lkdGg2/MDJoZWlnaHQ0MjAu/anBn"),
                new ProductData("Súp gà ngô", 70000.0, "Súp gà nấu với ngô ngọt, vị thanh nhẹ, dễ ăn.", "https://imgs.search.brave.com/gT_hzoKz_jT6dML0MDSw2p0GPipd3Bf7ib04OIMLp0s/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9hbmgu/MjRoLmNvbS52bi91/cGxvYWQvMi0yMDE3/L2ltYWdlcy8yMDE3/LTA1LTMwLzE0OTYx/MjM5MTUtMTQ5NTM1/NDgwMDI4NzE4LXN1/cC1nYS1uZ28tbmdv/dC0wMS5qcGc"),
                new ProductData("Gà tiềm thuốc bắc", 80000.0, "Gà hầm cùng các loại thuốc bắc, thơm đậm, bổ dưỡng và tốt cho sức khỏe.", "https://imgs.search.brave.com/CbBfvU2WMh05ZGTPasYoh1fSuojqe0jd4kRkzzUBGV8/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9saDct/dXMuZ29vZ2xldXNl/cmNvbnRlbnQuY29t/L2RvY3N6L0FEXzRu/WGNqeXVEUFR0NUFU/ZGZGSkdLQ2lqUEVn/UTB1TEdzdlR4N2t3/NmFhaDcyZ3pGbUVH/aHBMY2lWaGNJY0l1/eG9zdkM5VEpWWXVG/UEdrOEI5eEt6ZWFo/dkU2M3BqalRJUm1M/Q3pGUU1KWGRTU1FC/QlBXQ0p4dGt0NDJk/VkJVWkpJRmpUV3px/ak41VWlOWjUzZjhu/U1EwZ21wUE5OMWo_/a2V5PWcyZE9CdjI3/ZkxSUXlPWGFIZkNZ/THc")
        });

        // --- 6. Danh mục: Cơm-Mì-Cháo ---
        seedProductsForCategory("Cơm-Mì-Cháo", new ProductData[]{
                new ProductData("Cơm chả cua hoàng kim", 75000.0, "Cơm chiên vàng óng kết hợp chả cua thơm béo, đậm đà.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1cdb6151948324e7bb3b83f1b9f4cb.jpg?v=1667882448253"),
                new ProductData("Cơm đùi gà chiên giòn", 65000.0, "Đùi gà chiên giòn rụm, ăn kèm cơm nóng và nước mắm đậm vị.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/15b3ba303f9e14fc3a7300b794fb22.jpg?v=1667882426947"),
                new ProductData("Cơm sườn nướng", 65000.0, "Sườn heo ướp thấm vị, nướng thơm và mềm, ăn kèm cơm trắng.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1c912144730d24f13a35a91aa71c3c.jpg?v=1667882402897"),
                new ProductData("Bún bò", 65000.0, "Bún bò đậm đà với nước dùng béo thơm, thịt bò mềm ngon.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/12544e4d15a994948a261d455eee51.jpg?v=1667882376433"),
                new ProductData("Cháo bò bằm và trứng bắc thảo", 85000.0, "Cháo nóng sánh mịn, bò bằm thơm và trứng bắc thảo béo bùi.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1271b22e186634f45b1a5695ce5efb.jpg?v=1667882364173"),
                new ProductData("Hủ tiếu áp chảo bò", 110000.0, "Hủ tiếu áp chảo giòn nhẹ, xào cùng thịt bò mềm và sốt đậm vị.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/165aab637a80546a7925ad3c1d059f.jpg?v=1667882344460"),
                new ProductData("Mỳ spaghetti sốt kem nấm", 99000.0, "Spaghetti kem nấm béo ngậy, thơm dịu, phong cách Ý.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/16a823ee48d514262baa6de6adde8f.jpg?v=1667882287537"),
                new ProductData("Mì spaghetti sốt bò bằm", 99000.0, "Spaghetti sốt bò bằm truyền thống, đậm đà và thơm vị cà chua.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1bb2a25291b5542968dba015b4be31.jpg?v=1667882207860"),
                new ProductData("Cơm chiên thịt cua lá cẩm", 100000.0, "Cơm chiên lá cẩm tím đẹp mắt, kết hợp thịt cua ngọt tự nhiên.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/148cb1f9fc1dd41d49436d055639dc.jpg?v=1667882025500"),
                new ProductData("Cơm chiên hải sản", 99000.0, "Cơm chiên thơm với tôm, mực và nghêu, vị biển tươi ngon.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/2b8cb0f5f433c442c950c1aaf3f2e6.jpg?v=1667882001130"),
                new ProductData("Mì soba cá hồi", 165000.0, "Mì soba thanh nhẹ ăn kèm cá hồi áp chảo thơm béo.", "https://bizweb.dktcdn.net/thumb/large/100/469/097/products/1e8013ec25dbf4589bb3ee7d1f540e.jpg?v=1667881735857")
        });

        System.out.println("✅ Product Seeder: Đã hoàn tất nhập liệu sản phẩm.");
    }

    private void seedProductsForCategory(String categoryName, ProductData[] products) {
        // Tìm Category theo tên (Dùng hàm trong CategoryRepository)
        Optional<Category> categoryOpt = categoryRepository.findByName(categoryName);

        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            for (ProductData p : products) {
                // Kiểm tra nếu sản phẩm chưa tồn tại thì mới tạo
                if (!productRepository.existsByName(p.name)) {
                    Product product = new Product();
                    product.setName(p.name);
                    product.setPrice(p.price);
                    product.setDescription(p.description);
                    product.setImageUrl(p.imageUrl);
                    product.setCategory(category);

                    productRepository.save(product);
                }
            }
        } else {
            System.err.println("⚠️ Không tìm thấy danh mục: " + categoryName);
        }
    }

    // Class helper để chứa dữ liệu (giống DTO nhưng dùng nội bộ cho Seeder cho gọn)
    @lombok.AllArgsConstructor
    private static class ProductData {
        String name;
        Double price;
        String description;
        String imageUrl;
    }
}