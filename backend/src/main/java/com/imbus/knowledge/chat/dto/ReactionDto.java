package com.imbus.knowledge.chat.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionDto {
    private Long id;
    private String emoji;
    private UserInfoDto user;
}