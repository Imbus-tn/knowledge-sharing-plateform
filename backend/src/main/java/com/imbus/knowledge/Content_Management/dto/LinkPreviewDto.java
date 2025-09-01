// src/main/java/com/imbus/knowledge/Content_Management/dto/LinkPreviewDto.java
package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.Content_Management.entities.LinkPreview;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkPreviewDto {
    private String url;
    private String title;
    private String description;
    private String imageUrl;

    // ✅ Convert Entity → DTO
    public static LinkPreviewDto fromEntity(LinkPreview entity) {
        if (entity == null) return null;
        return LinkPreviewDto.builder()
                .url(entity.getUrl())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    // ✅ Convert DTO → Entity
    public static LinkPreview fromDto(LinkPreviewDto dto) {
        if (dto == null) return null;
        return LinkPreview.builder()
                .url(dto.getUrl())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .build();
    }
}