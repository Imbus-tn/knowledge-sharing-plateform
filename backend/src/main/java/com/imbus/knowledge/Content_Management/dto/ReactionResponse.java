// src/main/java/com/imbus/knowledge/Content_Management/dto/ReactionResponse.java
package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.User_Management.dto.UserSummaryDto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponse {
    private String emoji;
    private int count;
    private UserSummaryDto user; // Optional: if you want to show who reacted
}