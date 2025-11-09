package com.restaurant.restaurant_manager.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.restaurant.restaurant_manager.service.IStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CloudinaryAdapter implements IStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // Upload file lên Cloudinary
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        // Trả về URL của file đã upload
        return (String) uploadResult.get("url");
    }

    @Override
    public void deleteFile(String imageUrl) throws IOException {
        // Trích xuất public_id từ URL
        String publicId = extractPublicIdFromUrl(imageUrl);
        if (publicId != null) {
            // Xóa file khỏi Cloudinary bằng public_id
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    /**
     * Hàm helper để trích xuất public_id từ URL của Cloudinary
     * Ví dụ: http://.../v12345/folder/public_id.jpg -> trả về "folder/public_id"
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        // Pattern regex để tìm public_id (phần nằm sau /upload/v.../ và trước phần mở rộng file)
        Pattern pattern = Pattern.compile("/v\\d+/(.+?)\\.\\w{3,4}$");
        Matcher matcher = pattern.matcher(imageUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}