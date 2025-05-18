package com.imbus.knowledge.chat.repository;



import com.imbus.knowledge.chat.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
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
}