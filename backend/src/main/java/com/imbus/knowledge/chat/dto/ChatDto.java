package com.imbus.knowledge.chat.dto;



import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDto {
    private Long id;
    private String name;
    private boolean isGroup;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private int unreadCount;
    private MessagePreviewDto lastMessage;
    private Set<UserInfoDto> participants; // Changed to Set
}