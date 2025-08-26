package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.LinkPreviewDto;
import com.imbus.knowledge.Content_Management.services.LinkPreviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/link-preview")
@RequiredArgsConstructor
public class LinkPreviewController {

    private final LinkPreviewService linkPreviewService;

    @GetMapping
    public ResponseEntity<LinkPreviewDto> getPreview(@RequestParam String url) {
        try {
            LinkPreviewDto preview = linkPreviewService.fetchFromUrl(url);
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}