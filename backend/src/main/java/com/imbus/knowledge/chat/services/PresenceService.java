package com.imbus.knowledge.chat.services;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.chat.dto.UserPresenceDto;
import com.imbus.knowledge.chat.entities.Presence;
import com.imbus.knowledge.chat.repository.PresenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void userConnected(Long userId) {
        activeUsers.put(userId, LocalDateTime.now());
        updateAndBroadcast(userId, true);
    }

    @Transactional
    public void userDisconnected(Long userId) {
        activeUsers.remove(userId);
        updateAndBroadcast(userId, false);
    }

    private void updateAndBroadcast(Long userId, boolean isOnline) {
        userRepository.findById(userId).ifPresent(user -> {
            Presence presence = presenceRepository.findById(userId).orElse(new Presence());
            presence.setId(userId);
            presence.setOnline(isOnline);
            presence.setLastSeen(LocalDateTime.now());
            presenceRepository.save(presence);

            String avatarUrl = formatAvatarUrl(user.getAvatarUrl());
            UserPresenceDto dto = new UserPresenceDto(userId, isOnline, avatarUrl);
            webSocketService.notifyUserStatus(userId, dto);
        });
    }

    public UserPresenceDto getUserPresence(Long userId) {
        boolean isOnline = isUserOnline(userId);
        String avatarUrl = getAvatarUrl(userId);
        return new UserPresenceDto(userId, isOnline, avatarUrl);
    }

    private boolean isUserOnline(Long userId) {
        return activeUsers.containsKey(userId) &&
                activeUsers.get(userId).isAfter(LocalDateTime.now().minusMinutes(PRESENCE_TIMEOUT_MINUTES));
    }

    private String getAvatarUrl(Long userId) {
        return userRepository.findById(userId)
                .map(u -> formatAvatarUrl(u.getAvatarUrl()))
                .orElse("/uploads/default-avatar.jpg");
    }

    private String formatAvatarUrl(String avatarUrl) {
        if (avatarUrl == null) return "/uploads/default-avatar.jpg";
        return avatarUrl.startsWith("/uploads") ? avatarUrl : "/uploads" + (avatarUrl.startsWith("/") ? "" : "/") + avatarUrl;
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void cleanupStalePresence() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(PRESENCE_TIMEOUT_MINUTES);
        activeUsers.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
    }
}