package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.product.ProductResponse;
import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/admin/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("categoryId") UUID categoryId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        ProductResponse newProduct = productService.createProduct(name, price, description, categoryId, file);
        return ApiResponse.created(newProduct, "Product created successfully");
    }

    @PutMapping(value = "/admin/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) UUID categoryId,
            @RequestParam(value = "file", required = false) MultipartFile file // file không bắt buộc khi update
    ) throws IOException {
        ProductResponse updatedProduct = productService.updateProduct(id, name, price, description, categoryId, file);
        return ApiResponse.success(updatedProduct, "Product updated successfully");
    }


    @DeleteMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(@PathVariable UUID id) throws IOException {
        productService.deleteProduct(id);
        return ApiResponse.success(null, "Product deleted successfully");
    }

    @GetMapping("/public/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ApiResponse.success(products, "Products retrieved successfully");
    }


    @GetMapping("/public/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        ProductResponse product = productService.getProductById(id);
        return ApiResponse.success(product, "Product retrieved successfully");
    }



}