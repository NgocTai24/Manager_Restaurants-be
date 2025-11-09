package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.product.ProductResponse;
import com.restaurant.restaurant_manager.entity.Category;
import com.restaurant.restaurant_manager.entity.Product;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.CategoryRepository;
import com.restaurant.restaurant_manager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; // Dùng để tìm Category
    private final IStorageService storageService; // Tiêm Interface (Adapter Pattern)

    // Lấy tất cả (public)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Lấy 1 (public)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ProductResponse.fromEntity(product);
    }

    // Tạo (admin)
    public ProductResponse createProduct(String name, Double price, String description, UUID categoryId, MultipartFile file) throws IOException {
        // 1. Tìm Category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // 2. Upload ảnh (Sử dụng Adapter)
        String imageUrl = storageService.uploadFile(file);

        // 3. Tạo Product
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setCategory(category);
        product.setImageUrl(imageUrl);

        Product savedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(savedProduct);
    }

    // Cập nhật (admin)
    public ProductResponse updateProduct(UUID id, String name, Double price, String description, UUID categoryId, MultipartFile file) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }

        // Nếu có file mới, xóa file cũ và upload file mới
        if (file != null && !file.isEmpty()) {
            // Xóa ảnh cũ (Sử dụng Adapter)
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                storageService.deleteFile(product.getImageUrl());
            }
            // Upload ảnh mới
            String newImageUrl = storageService.uploadFile(file);
            product.setImageUrl(newImageUrl);
        }

        // Cập nhật các trường khác
        if (name != null) product.setName(name);
        if (price != null) product.setPrice(price);
        if (description != null) product.setDescription(description);

        Product updatedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(updatedProduct);
    }

    // Xóa (admin)
    public void deleteProduct(UUID id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Xóa ảnh trên Cloudinary (Sử dụng Adapter)
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            storageService.deleteFile(product.getImageUrl());
        }

        // Xóa trong DB
        productRepository.delete(product);
    }
}