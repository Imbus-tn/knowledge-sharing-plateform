package com.imbus.knowledge.User_Management.dto;

import com.imbus.knowledge.User_Management.entities.InvitationStatus;
import com.imbus.knowledge.User_Management.entities.UserRole;

import java.time.LocalDateTime;

public record InvitationDTO(
        Long id,
        String email,
        UserRole role,
        boolean used,
        LocalDateTime expiresAt,
        InvitationStatus status
) {}
