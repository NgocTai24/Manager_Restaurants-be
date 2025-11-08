package com.restaurant.restaurant_manager.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Bỏ qua các trường null khi serialize JSON
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;
    private List<String> errors;
//    private final LocalDateTime timestamp = LocalDateTime.now();

    // ----- Các hàm helper static để tạo response nhanh -----

    /**
     * Tạo một Response THÀNH CÔNG (200 OK)
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo một Response THÀNH CÔNG (201 Created) - Dùng khi TẠO MỚI
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Tạo một Response LỖI (chung)
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message, List<String> errors) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .statusCode(status.value())
                .message(message)
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Tạo một Response LỖI (cho 404 Not Found)
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message, null);
    }

    /**
     * Tạo một Response LỖI (cho 400 Bad Request)
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, List<String> errors) {
        return error(HttpStatus.BAD_REQUEST, message, errors);
    }
}