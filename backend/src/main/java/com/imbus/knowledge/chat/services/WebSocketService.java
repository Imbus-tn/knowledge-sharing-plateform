package com.imbus.knowledge.chat.services;

import com.imbus.knowledge.chat.dto.MessageDto;
import com.imbus.knowledge.chat.dto.UserPresenceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyNewMessage(MessageDto messageDto) {
        messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getChatId(), messageDto);
    }

    public void notifyUserStatus(Long userId, UserPresenceDto presence) {
        // Notify specific user
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/presence",
                presence
        );

        // Broadcast to all interested parties
        messagingTemplate.convertAndSend("/topic/presence", presence);
    }
}