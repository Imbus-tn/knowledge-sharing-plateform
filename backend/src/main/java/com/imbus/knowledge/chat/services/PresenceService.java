package com.imbus.knowledge.chat.services;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.chat.dto.UserPresenceDto;
import com.imbus.knowledge.chat.repository.PresenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PresenceService {
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    private final PresenceRepository presenceRepository;

    private final Map<Long, LocalDateTime> activeUsers = new ConcurrentHashMap<>();
    private static final int PRESENCE_TIMEOUT_MINUTES = 5;

    public void userConnected(Long userId) {
        activeUsers.put(userId, LocalDateTime.now());
        presenceRepository.saveUserPresence(userId, true);
        broadcastPresence(userId, true);
    }

    public void userDisconnected(Long userId) {
        activeUsers.remove(userId);
        presenceRepository.saveUserPresence(userId, false);
        broadcastPresence(userId, false);
    }

    public void refreshUserPresence(Long userId) {
        if (activeUsers.containsKey(userId)) {
            activeUsers.put(userId, LocalDateTime.now());
        }
    }

    public UserPresenceDto getUserPresence(Long userId) {
        boolean isOnline = isUserOnline(userId);
        String avatarUrl = getAvatarUrl(userId);
        return new UserPresenceDto(userId, isOnline, avatarUrl);
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void cleanupStalePresence() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(PRESENCE_TIMEOUT_MINUTES);
        activeUsers.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
    }

    private boolean isUserOnline(Long userId) {
        return activeUsers.containsKey(userId) &&
                activeUsers.get(userId).isAfter(LocalDateTime.now().minusMinutes(PRESENCE_TIMEOUT_MINUTES));
    }

    private String getAvatarUrl(Long userId) {
        return userRepository.findById(userId)
                .map(user -> formatAvatarUrl(user.getAvatarUrl()))
                .orElse("/uploads/default-avatar.jpg");
    }

    private String formatAvatarUrl(String avatarUrl) {
        if (avatarUrl == null) return "/uploads/default-avatar.jpg";
        return avatarUrl.startsWith("/uploads") ? avatarUrl : "/uploads" + (avatarUrl.startsWith("/") ? "" : "/") + avatarUrl;
    }

    private void broadcastPresence(Long userId, boolean isOnline) {
        UserPresenceDto presence = getUserPresence(userId);
        webSocketService.notifyUserStatus(userId, presence);
    }
}