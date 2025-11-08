package com.restaurant.restaurant_manager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmail;

            // 1. Kiểm tra header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2. Lấy token
            jwt = authHeader.substring(7);

            // 3. Giải mã (Đây là nơi có thể ném Exception)
            userEmail = jwtService.extractUsername(jwt);

            // 4. Xác thực
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 5. Kiểm tra token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // --- ĐÂY LÀ PHẦN SỬA LỖI QUAN TRỌNG ---
            // Bắt tất cả các lỗi (ExpiredJwtException, SignatureException, v.v...)
            // và trả về JSON 401 thay vì trang HTML

            // Log lỗi ra console để bạn debug
            System.err.println("Error in JwtAuthFilter: " + ex.getMessage());

            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("Unauthorized: " + ex.getMessage()) // Trả về thông báo lỗi thực tế
                    .build();

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().write(objectMapper.writeValueAsBytes(apiResponse));
        }
    }
}