package com.imbus.knowledge.User_Management.repositories;

import com.imbus.knowledge.User_Management.entities.Invitation;
import com.imbus.knowledge.User_Management.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByToken(String token);
    Optional<Invitation> findByEmailAndRole(String email, UserRole role);
    Optional<Invitation> findByEmail(String email);
}
