package com.imbus.knowledge.chat.repository;






import com.imbus.knowledge.chat.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id = :userId")
    List<Chat> findAllByParticipantId(@Param("userId") Long userId);

    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id = :userId AND c.isGroup = :isGroup")
    List<Chat> findAllByParticipantIdAndIsGroup(
            @Param("userId") Long userId,
            @Param("isGroup") boolean isGroup
    );

    boolean existsByIdAndParticipantsId(Long chatId, Long userId);
}