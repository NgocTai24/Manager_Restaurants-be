package com.restaurant.restaurant_manager.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NgrokRunner {

    @EventListener(ApplicationReadyEvent.class)
    public void startNgrok() {
        System.out.println("🚀 Đang khởi động Ngrok...");
        try {
            // Lệnh này chỉ chạy trên Windows
            // "cmd /c start": Mở một cửa sổ CMD mới
            // Sau đó chạy lệnh ngrok với domain cố định của bạn
            String command = "cmd.exe /c start ngrok http --domain=chang-cleavable-velia.ngrok-free.dev 8080";

            Runtime.getRuntime().exec(command);

            System.out.println("✅ Ngrok đã được bật tự động!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Không thể bật Ngrok: " + e.getMessage());
        }
    }
}