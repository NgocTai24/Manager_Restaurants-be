package com.restaurant.restaurant_manager.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IStorageService {

    String uploadFile(MultipartFile file) throws IOException;

    void deleteFile(String imageUrl) throws IOException;
}