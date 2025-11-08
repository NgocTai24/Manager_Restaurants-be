package com.restaurant.restaurant_manager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary // Đảm bảo Spring dùng bean này làm mặc định
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Đăng ký module để xử lý LocalDateTime (Java 8 Date/Time)
        mapper.registerModule(new JavaTimeModule());
        // Tắt tính năng viết ngày tháng dưới dạng timestamps (số)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}