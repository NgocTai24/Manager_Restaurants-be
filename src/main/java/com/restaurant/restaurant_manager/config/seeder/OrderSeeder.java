package com.restaurant.restaurant_manager.config.seeder;

import com.restaurant.restaurant_manager.entity.*;
import com.restaurant.restaurant_manager.entity.enums.OrderStatus;
import com.restaurant.restaurant_manager.entity.enums.PaymentMethod;
import com.restaurant.restaurant_manager.entity.enums.PaymentStatus;
import com.restaurant.restaurant_manager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class OrderSeeder {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    // Không cần OrderItemRepository vì cascade của Order sẽ tự lưu

    @Transactional
    public void seed() {
        if (orderRepository.count() == 0) {
            List<Customer> customers = customerRepository.findAll();
            List<Product> products = productRepository.findAll();

            if (customers.isEmpty() || products.isEmpty()) {
                System.out.println("⚠️ OrderSeeder: Cần có Customer và Product trước.");
                return;
            }

            Random random = new Random();

            // --- 1. Tạo đơn hàng QUÁ KHỨ (Để vẽ biểu đồ doanh thu) ---
            // Tạo 20 đơn ngẫu nhiên trong 6 tháng qua
            for (int i = 0; i < 20; i++) {
                Customer randomCustomer = customers.get(random.nextInt(customers.size()));
                // Random ngày trong quá khứ (từ 1 đến 180 ngày trước)
                LocalDateTime pastTime = LocalDateTime.now().minusDays(random.nextInt(180) + 1);

                // Trạng thái: Phần lớn là COMPLETED để có doanh thu
                OrderStatus status = (i % 5 == 0) ? OrderStatus.CANCELLED : OrderStatus.COMPLETED;

                createOrder(randomCustomer, products, pastTime, status, PaymentMethod.COD, random);
            }

            // --- 2. Tạo đơn hàng HÔM NAY (Để hiện Dashboard "Orders Today") ---
            // Đơn khách tự đặt (User/Registered Customer)
            // Giả sử customer[0] là khách có user
            createOrder(customers.get(0), products, LocalDateTime.now().minusHours(2), OrderStatus.COMPLETED, PaymentMethod.BANK_TRANSFER, random);

            // Đơn khách vãng lai (Guest) - Staff đặt giúp
            // Giả sử customer cuối cùng là guest
            Customer guest = customers.get(customers.size() - 1);
            createOrder(guest, products, LocalDateTime.now().minusMinutes(30), OrderStatus.PENDING, PaymentMethod.COD, random);

            System.out.println("✅ Order Seeder: Đã tạo dữ liệu đơn hàng mẫu.");
        }
    }

    private void createOrder(Customer customer, List<Product> products, LocalDateTime time, OrderStatus status, PaymentMethod paymentMethod, Random random) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderTime(time);
        order.setStatus(status);
        order.setNote("Order mẫu từ Seeder");
        order.setPaymentMethod(paymentMethod);

        // Tạo Order Items (Mỗi đơn mua 1-3 món ngẫu nhiên)
        List<OrderItem> items = new ArrayList<>();
        double totalAmount = 0.0;
        int numberOfItems = random.nextInt(3) + 1; // 1 đến 3 món

        for (int k = 0; k < numberOfItems; k++) {
            Product product = products.get(random.nextInt(products.size()));
            int quantity = random.nextInt(2) + 1; // 1 hoặc 2 cái

            OrderItem item = new OrderItem();
            item.setOrder(order); // Quan trọng: Link ngược lại Order
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPriceAtPurchase(product.getPrice()); // Lưu giá tại thời điểm mua

            items.add(item);
            totalAmount += product.getPrice() * quantity;
        }

        order.setOrderItems(items);
        order.setTotalAmount(totalAmount);

        // Tạo Payment giả (nếu đơn đã hoàn thành hoặc đang xử lý)
        // Lưu ý: Payment cũng cần được lưu. Nếu bạn có cascade ở Order -> Payment thì set vào đây.
        // Entity Order của bạn: @OneToOne(mappedBy = "order", cascade = CascadeType.ALL) private Payment payment;
        // Nên ta sẽ tạo Payment và set vào Order
        if (status != OrderStatus.CANCELLED) {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(totalAmount);
            payment.setPaymentMethod(paymentMethod);
            payment.setStatus(status == OrderStatus.COMPLETED ? PaymentStatus.PAID : PaymentStatus.UNPAID);
            payment.setCreatedAt(time);
            if (status == OrderStatus.COMPLETED) {
                payment.setPaidAt(time.plusMinutes(5));
            }
            order.setPayment(payment);
        }

        orderRepository.save(order);
    }
}