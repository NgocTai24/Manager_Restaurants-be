package com.restaurant.restaurant_manager.exception;

import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice // Báo cho Spring Boot biết đây là nơi xử lý lỗi toàn cục
public class GlobalExceptionHandler {

    /**
     * Bắt lỗi 'ResourceNotFoundException' (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ApiResponse.notFound(ex.getMessage());
    }

    /**
     * Bắt lỗi 'BadRequestException' (400)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex) {
        return ApiResponse.badRequest(ex.getMessage(), null);
    }

    /**
     * Bắt lỗi VALIDATION (khi dùng @Valid trong DTO) (400)
     * Đây là lỗi quan trọng nhất khi làm việc với DTO.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // Lấy danh sách các lỗi chi tiết
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        return ApiResponse.badRequest("Validation failed", errors);
    }

    /**
     * Bắt tất cả các lỗi 500 (Lỗi máy chủ)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        // Log lỗi này ra console (quan trọng)
        ex.printStackTrace();

        return ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal server error occurred: " + ex.getMessage(),
                null
        );
    }
}