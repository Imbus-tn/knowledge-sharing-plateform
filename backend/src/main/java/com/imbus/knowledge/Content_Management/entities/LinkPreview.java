// src/main/java/com/imbus/knowledge/Content_Management/entities/LinkPreview.java
package com.imbus.knowledge.Content_Management.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "link_previews")
public class LinkPreview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    private String title;
    private String description;
    private String imageUrl;

    // Optional: Reference back to Post
    @OneToOne(mappedBy = "linkPreview", cascade = CascadeType.ALL, orphanRemoval = true)
    private Post post;
}