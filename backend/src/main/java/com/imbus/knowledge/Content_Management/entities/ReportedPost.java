package com.imbus.knowledge.Content_Management.entities;

import com.imbus.knowledge.User_Management.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportedPost {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Post post;

    @ManyToOne
    private User reporter;

    private String reason;

    private LocalDateTime reportedAt;
}
