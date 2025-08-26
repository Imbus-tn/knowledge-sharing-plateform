package com.imbus.knowledge.Notification.repository;

import com.imbus.knowledge.Notification.entity.Notification;
import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user = :user AND n.read = false")
    void markAllAsRead(@Param("user") User user);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :id AND n.user = :user")
    void markAsRead(@Param("id") Long id, @Param("user") User user);

    long countByUserAndReadFalse(User user);

    @Query("SELECT COUNT(n) > 0 FROM Notification n WHERE n.user = :user AND n.createdAt > :since")
    boolean hasNewNotifications(@Param("user") User user, @Param("since") LocalDateTime since);
}