package com.imbus.knowledge.chat.services;

import com.imbus.knowledge.chat.dto.FileUploadResponse;
import com.imbus.knowledge.chat.exception.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${file.upload-dir:uploads/chat_uploads}")
    private String uploadDir;

    public FileUploadResponse storeFile(MultipartFile file) throws FileUploadException {
        try {
            if (file == null || file.isEmpty()) {
                throw new FileUploadException("File is empty");
            }

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String fileName = generateUniqueFileName(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return new FileUploadResponse(
                    fileName,
                    "/api/chat/files/" + fileName,
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException ex) {
            throw new FileUploadException("Could not store file: " + ex.getMessage());
        }
    }

    public Path loadFile(String filename) {
        return Paths.get(uploadDir).resolve(filename).normalize();
    }

    public void deleteFile(String filename) throws IOException {
        Path filePath = loadFile(filename);
        Files.deleteIfExists(filePath);
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }
}