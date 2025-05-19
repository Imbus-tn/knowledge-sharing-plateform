package com.imbus.knowledge.chat.entities;

import jakarta.persistence.Embeddable;
import lombok.*;
import jakarta.persistence.Column;
@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attachment {
    @Column(name = "attachment_type")
    private String type;

    @Column(name = "attachment_url")
    private String url;

    @Column(name = "attachment_name")
    private String name;

    @Column(name = "attachment_size")
    private Long size;
}