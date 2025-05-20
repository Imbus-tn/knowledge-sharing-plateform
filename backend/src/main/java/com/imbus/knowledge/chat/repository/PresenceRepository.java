package com.imbus.knowledge.chat.repository;

public interface PresenceRepository {
    void saveUserPresence(Long userId, boolean isOnline);
    Boolean getUserPresence(Long userId);
}