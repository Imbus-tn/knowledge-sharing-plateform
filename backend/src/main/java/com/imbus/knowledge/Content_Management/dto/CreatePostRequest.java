package com.imbus.knowledge.Content_Management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequest {
    private String title;           // Add title
    private String content;
    private String imageUrl;
    private String videoUrl;        // Add video URL
    private String videoThumbnail;  // Add video thumbnail
    private List<TagDto> tags;      // Add tags
}