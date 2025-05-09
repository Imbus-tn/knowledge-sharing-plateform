package com.imbus.knowledge.User_Management.dto;

import com.imbus.knowledge.User_Management.entities.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvitationResponse {
    private String email;
    private UserRole role;
}
