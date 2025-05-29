package com.imbus.knowledge.Content_Management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionDto {
    private String emoji;      // e.g., "👍", "❤️"
    private int count;         // total users who reacted with this emoji
    private List<String> users; // list of user IDs who used this emoji
}
