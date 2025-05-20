package com.imbus.knowledge.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPresenceDto {
    private Long userId;
    private boolean isOnline;
    private String avatarUrl;
}