package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.category.CategoryRequest;
import com.restaurant.restaurant_manager.dto.category.CategoryResponse;
import com.restaurant.restaurant_manager.dto.response.PageResponse;
import com.restaurant.restaurant_manager.entity.Category;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public PageResponse<CategoryResponse> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryResponse> content = categoryPage.getContent().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.<CategoryResponse>builder()
                .content(content)
                .pageNo(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .last(categoryPage.isLast())
                .build();
    }

    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        return categoryRepository.save(category);
    }

    public Category updateCategory(UUID id, CategoryRequest request) {
        Category category = getCategoryById(id);
        category.setName(request.getName());
        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
