// src/main/java/com/imbus/knowledge/Content_Management/dto/ShareResponse.java
package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.User_Management.dto.UserSummaryDto;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareResponse {
    private Long id;
    private UserSummaryDto user;
    private LocalDateTime sharedAt;
}