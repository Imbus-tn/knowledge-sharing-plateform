package com.imbus.knowledge.Notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDto {
    private Long id;
    private String message;
    private boolean read;
    private String createdAt;
    private String link;
    private String type;
    private UserDto user;
    private Long postId;
    private String postTitle;
    private Long userId;
}