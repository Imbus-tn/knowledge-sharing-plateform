package com.imbus.knowledge.chat.repository;

import com.imbus.knowledge.chat.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    // Existing query methods
    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id = :userId")
    List<Chat> findAllByParticipantId(@Param("userId") Long userId);

    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id = :userId AND c.isGroup = :isGroup")
    List<Chat> findAllByParticipantIdAndIsGroup(
            @Param("userId") Long userId,
            @Param("isGroup") boolean isGroup
    );

    // Existing derived method
    boolean existsByIdAndParticipantsId(Long chatId, Long userId);

    // New derived methods to add
    List<Chat> findByParticipants_Id(Long userId);

    boolean existsByIdAndParticipants_Id(Long chatId, Long userId);

    // Additional useful methods
    @Query("SELECT COUNT(c) > 0 FROM Chat c JOIN c.participants p WHERE c.id = :chatId AND p.id = :userId")
    boolean isUserInChat(@Param("chatId") Long chatId, @Param("userId") Long userId);

    List<Chat> findByParticipants_IdAndIsGroup(Long userId, boolean isGroup);
}