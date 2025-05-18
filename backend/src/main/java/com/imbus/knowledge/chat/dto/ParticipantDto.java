package com.imbus.knowledge.chat.dto;



import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantDto {
    private Long id;
    private String name;
    private String initials;
    private String avatarUrl;
    private boolean online;
    private String lastSeen;
}