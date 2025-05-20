package com.imbus.knowledge.chat.controller;

import com.imbus.knowledge.chat.dto.MessageDto;
import com.imbus.knowledge.chat.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(
            @RequestParam Long chatId,
            @RequestParam Long senderId,
            @RequestParam String content,
            @RequestParam(required = false) Long replyToId,
            @RequestParam(required = false) MultipartFile file) {
        MessageDto message = messageService.sendMessage(chatId, senderId, content, replyToId, file);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);
        return ResponseEntity.ok(message);
    }

    @MessageMapping("/chat.sendMessage")
    public void handleChatMessage(@Payload MessageDto messageDto) {
        MessageDto savedMessage = messageService.sendMessage(
                messageDto.getChatId(),
                messageDto.getSender().getId(),
                messageDto.getContent(),
                messageDto.getReplyTo() == null ? null : messageDto.getReplyTo().getId(),
                null); // No file for WebSocket messages
        messagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getChatId(), savedMessage);
    }
}