package com.restaurant.restaurant_manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int pageNo;       // Trang hiện tại
    private int pageSize;     // Kích thước trang
    private int totalPages;   // Tổng số trang
    private long totalElements; // Tổng số bản ghi
    private boolean last;     // Có phải trang cuối không?
    private List<T> content;  // Dữ liệu chính (UserResponse, ProductResponse...)
}