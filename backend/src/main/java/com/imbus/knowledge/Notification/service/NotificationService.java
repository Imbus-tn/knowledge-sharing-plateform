package com.imbus.knowledge.Notification.service;

import com.imbus.knowledge.Notification.dto.NotificationDto;
import com.imbus.knowledge.Notification.dto.UserDto;
import com.imbus.knowledge.Notification.entity.Notification;
import com.imbus.knowledge.Notification.entity.NotificationType;
import com.imbus.knowledge.Notification.repository.NotificationRepository;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.chat.repository.MessageRepository;
import com.imbus.knowledge.Content_Management.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostReactionRepository reactionRepository;
    private final FavoriteRepository favoriteRepository;

    public List<NotificationDto> getUserNotifications(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByUserAndReadFalse(user);
    }

    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        notificationRepository.markAllAsRead(user);

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                Map.of("type", "all-read", "message", "All notifications marked as read")
        );
    }

    public void markAsRead(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        notificationRepository.markAsRead(id, user);
    }

    public boolean hasNewNotifications(Long userId, LocalDateTime lastChecked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.hasNewNotifications(user, lastChecked);
    }

    public void createNotification(
            Long recipientId,
            String message,
            NotificationType type,
            String link,
            Long relatedId,
            String relatedType,
            Long senderId) {

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        User sender = userRepository.findById(senderId)
                .orElse(null);

        Notification notification = Notification.builder()
                .user(recipient)
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .link(link)
                .type(type)
                .relatedId(relatedId)
                .relatedType(relatedType)
                .build();

        notificationRepository.save(notification);

        // Send real-time via WebSocket
        messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/notifications",
                convertToDto(notification)
        );
    }

    private NotificationDto convertToDto(Notification notification) {
        User sender = null;
        String relatedType = notification.getRelatedType();
        Long relatedId = notification.getRelatedId();

        if (relatedType != null && relatedId != null) {
            switch (relatedType.toLowerCase()) {
                case "message":
                    var message = messageRepository.findById(relatedId).orElse(null);
                    if (message != null) sender = message.getSender();
                    break;
                case "post":
                    var post = postRepository.findById(relatedId).orElse(null);
                    if (post != null) sender = post.getAuthor();
                    break;
                case "comment":
                    var comment = commentRepository.findById(relatedId).orElse(null);
                    if (comment != null) sender = comment.getAuthor();
                    break;
                case "reaction":
                    var reaction = reactionRepository.findById(relatedId).orElse(null);
                    if (reaction != null) sender = reaction.getUser();
                    break;
                case "favorite":
                    var favorite = favoriteRepository.findById(relatedId).orElse(null);
                    if (favorite != null) sender = favorite.getUser();
                    break;
            }
        }

        return NotificationDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .link(notification.getLink())
                .type(notification.getType().name())
                .user(sender != null ? UserDto.builder()
                        .name(sender.getName())
                        .initials(extractInitials(sender.getName()))
                        .avatar(sender.getAvatarUrl())
                        .build() : null)
                .postId("post".equals(relatedType) ? relatedId : null)
                .userId(sender != null ? sender.getId() : null)
                .build();
    }

    private String extractInitials(String name) {
        if (name == null || name.isEmpty()) return "UN";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return parts[0].substring(0, 1).toUpperCase() + parts[parts.length - 1].substring(0, 1).toUpperCase();
    }
}