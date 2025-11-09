package com.restaurant.restaurant_manager.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Interface cho Dịch vụ Lưu trữ (Adapter Pattern)
 * Định nghĩa các hành vi chuẩn cho việc upload và xóa file.
 */
public interface IStorageService {

    /**
     * Upload một file.
     * @param file File được gửi lên
     * @return URL công khai (public URL) của file đã upload
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * Xóa một file dựa trên URL của nó.
     * @param imageUrl URL công khai của file cần xóa
     */
    void deleteFile(String imageUrl) throws IOException;
}