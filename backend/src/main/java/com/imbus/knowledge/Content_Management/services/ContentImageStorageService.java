
package com.imbus.knowledge.Content_Management.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ContentImageStorageService {

    @Value("${app.upload-dir:uploads/}")
    private String uploadDir;

    public String storeImage(MultipartFile file) throws IOException {
        // Validate image
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Create uploads directory
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // Generate unique filename
        String fileName = UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(fileName);

        // Copy file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path for DB
        return "/content/images/" + fileName;
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}