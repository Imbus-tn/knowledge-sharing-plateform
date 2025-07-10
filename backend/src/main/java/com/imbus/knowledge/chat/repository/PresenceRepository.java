package com.imbus.knowledge.chat.repository;

import com.imbus.knowledge.chat.entities.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, Long> {
    // Optional custom queries
}