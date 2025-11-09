package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.category.CategoryRequest;
import com.restaurant.restaurant_manager.dto.category.CategoryResponse;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ApiResponse.success(categories, "Categories retrieved successfully");
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