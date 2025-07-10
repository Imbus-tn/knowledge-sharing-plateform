package com.imbus.knowledge.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPresenceDto {
    private Long userId;
    private boolean online;
    private String avatarUrl;
}