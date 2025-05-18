package com.imbus.knowledge.chat.dto;



import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentDto {
    private String type;
    private String url;
    private String name;
    private Long size;
}