// src/main/java/com/imbus/knowledge/Content_Management/services/ContentImageStorageService.java
package com.imbus.knowledge.Content_Management.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ContentImageStorageService {

    public static final String UPLOAD_DIR = "uploads/posts/";

    public ContentImageStorageService() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    // ✅ Rename or add this method to match controller usage
    public String storeImage(MultipartFile file) { // ← This is what your controller expects
        return storePostImage(file);
    }

    public String storePostImage(MultipartFile file) {
        try {
            if (!file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }

            String fileName = UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/posts/" + fileName; // This path is correct
        } catch (IOException e) {
            throw new RuntimeException("Failed to store post image", e);
        }
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null) return ".jpg";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        return lastDotIndex > 0 ? originalFilename.substring(lastDotIndex) : ".jpg";
    }
}