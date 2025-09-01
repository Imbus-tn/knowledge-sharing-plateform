// src/main/java/com/imbus/knowledge/Content_Management/dto/CreatePostRequest.java
package com.imbus.knowledge.Content_Management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePostRequest {
    private String content;
    private String imageUrl;
    private String title;
    private String description;
    private String category;
    private List<String> tags;

    // For link sharing
    private String linkUrl;
    private LinkPreviewDto linkPreview;
}