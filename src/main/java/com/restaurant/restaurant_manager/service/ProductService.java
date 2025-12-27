package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.product.ProductResponse;
import com.restaurant.restaurant_manager.dto.response.PageResponse;
import com.restaurant.restaurant_manager.entity.Category;
import com.restaurant.restaurant_manager.entity.Product;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.CategoryRepository;
import com.restaurant.restaurant_manager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final CategoryRepository categoryRepository;
    private final IStorageService storageService;

    public PageResponse<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponse> content = productPage.getContent().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNo(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ProductResponse.fromEntity(product);
    }

    // Tạo (admin)
    public ProductResponse createProduct(String name, Double price, String description, UUID categoryId, MultipartFile file) throws IOException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String imageUrl = storageService.uploadFile(file);

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

        if (file != null && !file.isEmpty()) {
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                storageService.deleteFile(product.getImageUrl());
            }
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
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            storageService.deleteFile(product.getImageUrl());
        }
        productRepository.delete(product);
    }

    public PageResponse<ProductResponse> getProductsByCategory(UUID categoryId, int page, int size) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        List<ProductResponse> content = productPage.getContent().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());

        // Đóng gói PageResponse (Giống hệt hàm getAllProducts)
        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNo(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    public List<ProductResponse> getFeaturedProducts(int limit) {
        // Lấy top 'limit' sản phẩm (ví dụ: top 5, top 10)
        Pageable pageable = PageRequest.of(0, limit);

        List<Product> topProducts = productRepository.findTopSellingProducts(pageable);

        // Convert sang DTO để trả về cho Client
        return topProducts.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
}