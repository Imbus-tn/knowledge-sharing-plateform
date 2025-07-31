package com.imbus.knowledge.User_Management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WarningMessageDto {
    private Long id;
    private String message;
    private String adminName;
    private LocalDateTime createdAt;
    private boolean isResolved;
}