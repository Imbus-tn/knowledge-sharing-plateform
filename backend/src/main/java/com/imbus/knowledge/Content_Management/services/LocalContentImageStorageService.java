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
public class LocalContentImageStorageService {
    public static final String UPLOAD_DIR = "uploads/content/";

    public LocalContentImageStorageService() {
        // Ensure the upload directory exists
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    public String storeImage(MultipartFile file) {
        try {
            // Validate image type
            if (!file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }

            // Generate unique filename
            String fileName = UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // Save file to the server
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the relative path (e.g., "/uploads/content/abc123.jpg")
            return "/uploads/content/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store content image", e);
        }
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null) {
            return ".jpg";
        }
        int lastDotIndex = originalFilename.lastIndexOf(".");
        return lastDotIndex > 0 ? originalFilename.substring(lastDotIndex) : ".jpg";
    }
}
