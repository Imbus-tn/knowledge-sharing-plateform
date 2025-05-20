package com.imbus.knowledge.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDto {
    private Long id;
    private String name;
    private String avatarUrl;
    private boolean online;
}