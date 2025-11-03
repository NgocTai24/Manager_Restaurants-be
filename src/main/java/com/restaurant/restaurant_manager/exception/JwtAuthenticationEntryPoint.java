package com.restaurant.restaurant_manager.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        // Tạo response 401
        ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Authentication Failed: Full authentication is required to access this resource.")
                .build();

        // Dùng ObjectMapper để ghi JSON vào response
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(apiResponse));
    }
}