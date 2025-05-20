package com.imbus.knowledge.chat.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private Long chatId;
    private UserInfoDto sender;
    private String content;
    private Set<ReactionDto> reactions;
    private MessagePreviewDto replyTo;
    private LocalDateTime sentAt;
    private boolean read;
    private AttachmentDto attachment;
}