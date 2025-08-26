package com.imbus.knowledge.Content_Management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkPreviewDto {
    private String url;
    private String title;
    private String description;
    private String imageUrl;
}
