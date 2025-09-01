package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.User_Management.dto.UserSummaryDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String text;
    private UserSummaryDto author;
    private LocalDateTime createdAt;
    private List<CommentResponse> replies;
    private List<ReactionResponse> reactions;
    private boolean isEdited = false;
}