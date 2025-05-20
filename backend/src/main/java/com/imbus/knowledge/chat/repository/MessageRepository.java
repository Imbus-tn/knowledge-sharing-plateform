package com.imbus.knowledge.chat.repository;

import com.imbus.knowledge.chat.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Existing methods
    List<Message> findByChatIdOrderBySentAtAsc(Long chatId);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.read = false AND m.sender.id != :userId")
    List<Message> findUnreadMessages(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId AND m.read = false AND m.sender.id != :userId")
    int countUnreadMessages(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId
    );

    @Modifying
    @Query("UPDATE Message m SET m.read = :read WHERE m.chat.id = :chatId AND m.sender.id <> :userId")
    void updateReadStatus(Long chatId, Long userId, boolean read);

    // New methods to add
    List<Message> findByChatIdOrderBySentAtDesc(Long chatId);

    boolean existsByChatIdAndSenderId(Long chatId, Long senderId);
}