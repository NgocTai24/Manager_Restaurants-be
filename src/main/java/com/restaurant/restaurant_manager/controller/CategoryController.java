package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.category.CategoryRequest;
import com.restaurant.restaurant_manager.dto.category.CategoryResponse;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.dto.response.PageResponse;
import com.restaurant.restaurant_manager.entity.Category;
import com.restaurant.restaurant_manager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category newCategory = categoryService.createCategory(request);
        return ApiResponse.created(newCategory, "Category created successfully");
    }

    @GetMapping("/public/categories")
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<CategoryResponse> categories = categoryService.getAllCategories(page, size);
        return ApiResponse.success(categories, "Categories retrieved successfully");
    }

    @GetMapping("/public/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.getCategoryById(id);
        CategoryResponse response = CategoryResponse.fromEntity(category);
        return ApiResponse.success(response, "Category retrieved successfully");
    }


    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request
    ) {
        Category updatedCategory = categoryService.updateCategory(id, request);
        return ApiResponse.success(updatedCategory, "Category updated successfully");
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(null, "Category deleted successfully");
    }
}