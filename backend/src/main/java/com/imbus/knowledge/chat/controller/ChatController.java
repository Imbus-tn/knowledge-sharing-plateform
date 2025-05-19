package com.imbus.knowledge.chat.controller;



import com.imbus.knowledge.chat.dto.ChatDto;
import com.imbus.knowledge.chat.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.List;


@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatDto>> getUserChats(@RequestParam Long userId) {
        return ResponseEntity.ok(chatService.getUserChats(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatDto> getChat(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(chatService.getChatById(id, userId));
    }

    @PostMapping
    public ResponseEntity<ChatDto> createChat(
            @RequestParam Set<Long> participantIds,
            @RequestParam(required = false) String chatName) {
        return ResponseEntity.ok(chatService.createChat(participantIds, chatName));
    }
}