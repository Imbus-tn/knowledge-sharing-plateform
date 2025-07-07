package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShareRepository extends JpaRepository<Share, Long> {

    // NEW QUERY ↓↓↓
    @Query("SELECT COUNT(s) FROM Share s WHERE s.user.id = ?1")
    int countByUserId(Long userId);
}