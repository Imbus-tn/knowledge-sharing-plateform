package com.imbus.knowledge.chat.dto;



import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessagePreviewDto {
    private Long id;
    private String preview;
    private LocalDateTime sentAt;
    private UserInfoDto sender;
}