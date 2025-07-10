package com.imbus.knowledge.chat.services;

import com.imbus.knowledge.chat.dto.UserPresenceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUserStatus(Long userId, UserPresenceDto presenceDto) {
        messagingTemplate.convertAndSend("/topic/presence/" + userId, presenceDto);
    }

    public void broadcastUserStatus(UserPresenceDto presenceDto) {
        messagingTemplate.convertAndSend("/topic/presence", presenceDto);
    }
}