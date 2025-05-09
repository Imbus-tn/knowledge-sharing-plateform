package com.imbus.knowledge.Content_Management.entities;

import com.imbus.knowledge.User_Management.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    @Id @GeneratedValue
    private Long id;

    private String type; // like, love, wow...

    @ManyToOne
    private Post post;

    @ManyToOne
    private Comment comment;

    @ManyToOne
    private User user;

    private LocalDateTime createdAt;
}
