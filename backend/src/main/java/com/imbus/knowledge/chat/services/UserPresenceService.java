package com.imbus.knowledge.chat.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserPresenceService {
    private final Map<Long, Boolean> onlineStatusMap = new ConcurrentHashMap<>();

    public void setUserOnline(Long userId) {
        onlineStatusMap.put(userId, true);
    }

    public void setUserOffline(Long userId) {
        onlineStatusMap.put(userId, false);
    }

    public boolean isUserOnline(Long userId) {
        return onlineStatusMap.getOrDefault(userId, false);
    }
}