package com.imbus.knowledge.Notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String name;
    private String initials;
    private String avatar;
}