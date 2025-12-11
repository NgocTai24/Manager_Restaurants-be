package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.news.NewsDTOs.*;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // 1. Xem danh sách tin tức (Public - Khách chưa đăng nhập cũng xem được)
    @GetMapping("/public/news")
    public ResponseEntity<ApiResponse<List<NewsResponse>>> getAllNews() {
        return ApiResponse.success(newsService.getAllNews(), "Latest news retrieved");
    }

    // 2. Xem chi tiết tin tức (Public)
    @GetMapping("/public/news/{id}")
    public ResponseEntity<ApiResponse<NewsResponse>> getNewsById(@PathVariable UUID id) {
        return ApiResponse.success(newsService.getNewsById(id), "News details retrieved");
    }

    // 3. Tạo tin tức (Chỉ Staff/Admin)
    @PostMapping("/staff/news")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<NewsResponse>> createNews(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateNewsRequest request
    ) {
        return ApiResponse.created(newsService.createNews(user, request), "News created successfully");
    }

    // 4. Cập nhật tin tức (Chỉ Staff/Admin)
    @PutMapping("/staff/news/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<NewsResponse>> updateNews(
            @PathVariable UUID id,
            @RequestBody UpdateNewsRequest request
    ) {
        return ApiResponse.success(newsService.updateNews(id, request), "News updated successfully");
    }

    // 5. Xóa tin tức (Chỉ Admin)
    @DeleteMapping("/admin/news/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteNews(@PathVariable UUID id) {
        newsService.deleteNews(id);
        return ApiResponse.success(null, "News deleted successfully");
    }
}