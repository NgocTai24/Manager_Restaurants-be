package com.restaurant.restaurant_manager.config;

import com.restaurant.restaurant_manager.exception.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity // Cho phép dùng @PreAuthorize trên các Controller
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF (vì dùng JWT, không dùng session/cookie)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Cấu hình xử lý lỗi (EntryPoint 401)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // 3. Cấu hình phân quyền (Authorization)
                .authorizeHttpRequests(auth -> auth
                        // Cho phép các API này mà không cần đăng nhập
                        .requestMatchers(
                                "/api/v1/auth/**", // Các API đăng nhập, đăng ký
                                "/api/v1/public/**", // Các API public (ví dụ: xem menu)
                                // (Các URL cho Swagger/OpenAPI - nếu dùng)
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Yêu cầu quyền ADMIN cho các API admin
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Yêu cầu quyền STAFF hoặc ADMIN
                        .requestMatchers("/api/v1/staff/**").hasAnyRole("ADMIN", "STAFF")

                        // Tất cả các request còn lại đều phải đăng nhập
                        .anyRequest().authenticated()
                )

                // 4. Cấu hình Session (STATELESS - không dùng session)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Thêm Authentication Provider
                .authenticationProvider(authenticationProvider)

                // 6. Thêm Filter JWT (quan trọng nhất)
                // Chạy filter của ta TRƯỚC filter UsernamePassword...
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}