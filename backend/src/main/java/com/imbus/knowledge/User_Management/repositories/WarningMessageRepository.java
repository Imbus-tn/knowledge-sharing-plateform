package com.imbus.knowledge.User_Management.repositories;

import com.imbus.knowledge.User_Management.entities.WarningMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarningMessageRepository extends JpaRepository<WarningMessage, Long> {
    List<WarningMessage> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<WarningMessage> findByAdminIdOrderByCreatedAtDesc(Long adminId);
    List<WarningMessage> findByResolvedFalse();// Get unresolved warnings
}