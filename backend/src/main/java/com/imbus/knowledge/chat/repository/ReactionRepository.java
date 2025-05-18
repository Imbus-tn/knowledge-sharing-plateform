package com.imbus.knowledge.chat.repository;



import com.imbus.knowledge.chat.entities.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    @Modifying
    @Query("DELETE FROM Reaction r WHERE r.message.id = :messageId AND r.user.id = :userId")
    void deleteByMessageIdAndUserId(
            @Param("messageId") Long messageId,
            @Param("userId") Long userId
    );

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reaction r WHERE r.message.id = :messageId AND r.user.id = :userId")
    boolean existsByMessageIdAndUserId(
            @Param("messageId") Long messageId,
            @Param("userId") Long userId
    );
}