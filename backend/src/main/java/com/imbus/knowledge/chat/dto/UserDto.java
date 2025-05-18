package com.imbus.knowledge.chat.dto;



import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String avatarUrl;
    private boolean online;
    private String initials;
}
