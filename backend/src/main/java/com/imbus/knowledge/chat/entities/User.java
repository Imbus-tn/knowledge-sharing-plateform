package com.imbus.knowledge.chat.entities;


import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String name;
    private String avatarUrl;
    private boolean online;

    @ManyToMany(mappedBy = "participants")
    @Builder.Default
    private Set<Chat> chats = new HashSet<>();
}