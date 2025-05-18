package com.imbus.knowledge.chat.controller;


import com.imbus.knowledge.chat.dto.MessageDto;
import com.imbus.knowledge.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<List<MessageDto>> getChatMessages(
            @RequestParam Long chatId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(messageService.getChatMessages(chatId, userId));
    }

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(
            @RequestParam Long chatId,
            @RequestParam Long senderId,
            @RequestParam String content,
            @RequestParam(required = false) Long replyToId) {
        MessageDto message = messageService.sendMessage(chatId, senderId, content, replyToId);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);
        return ResponseEntity.ok(message);
    }

    @MessageMapping("/chat.sendMessage")
    public void handleChatMessage(@Payload MessageDto messageDto) {
        MessageDto savedMessage = messageService.sendMessage(
                messageDto.getChatId(),
                messageDto.getSender().getId(),
                messageDto.getContent(),
                messageDto.getReplyTo() == null ? null : messageDto.getReplyTo().getId());
        messagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getChatId(), savedMessage);
    }
}