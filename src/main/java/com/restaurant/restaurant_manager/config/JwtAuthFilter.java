package com.restaurant.restaurant_manager.config;

import com.restaurant.restaurant_manager.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Lấy từ ApplicationConfig

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Kiểm tra xem có header 'Authorization' và có 'Bearer' không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Không có token, cho qua
            return;
        }

        // 2. Lấy token (bỏ "Bearer " đi)
        jwt = authHeader.substring(7);

        // 3. Giải mã email (username)
        userEmail = jwtService.extractUsername(jwt);

        // 4. Nếu có email VÀ user chưa được xác thực (SecurityContextHolder)
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Lấy UserDetails (User) từ DB
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5. Nếu token hợp lệ
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Tạo một "vé" xác thực
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Không cần credentials
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Đưa "vé" này cho Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response); // Cho request đi tiếp
    }
}