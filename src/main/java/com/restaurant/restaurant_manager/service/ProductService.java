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
        // Sắp xếp theo tên sản phẩm
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Product> productPage = productRepository.findAll(pageable);

        // Convert Entity -> DTO
        List<ProductResponse> content = productPage.getContent().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());

        // Đóng gói vào PageResponse
        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNo(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
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

    public PageResponse<ProductResponse> getProductsByCategory(UUID categoryId, int page, int size) {
        // Kiểm tra Category có tồn tại không (Optional, nhưng nên có để báo lỗi rõ ràng)
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found");
        }

        // Sắp xếp theo tên A-Z (hoặc theo ý bạn)
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        // Gọi Repository
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        // Convert Entity -> DTO
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
}