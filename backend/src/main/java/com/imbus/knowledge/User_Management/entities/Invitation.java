package com.imbus.knowledge.User_Management.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean used;

    @Transient
    public InvitationStatus getStatus() {
        if (used) return InvitationStatus.ACCEPTED;
        if (expiresAt.isBefore(LocalDateTime.now())) return InvitationStatus.EXPIRED;
        return InvitationStatus.PENDING;
    }

}
