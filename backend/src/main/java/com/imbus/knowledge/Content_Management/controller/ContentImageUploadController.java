package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.services.LocalContentImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class ContentImageUploadController {
    private final LocalContentImageStorageService imageStorageService;

    @PostMapping("/image")
    public ResponseEntity<String> uploadContentImage(@RequestParam("file") MultipartFile file) {
        String fileUrl = imageStorageService.storeImage(file);
        return ResponseEntity.ok(fileUrl);
    }
}
