package com.imbus.knowledge.chat.controller;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import com.imbus.knowledge.chat.dto.ChatDto;
import com.imbus.knowledge.chat.dto.CreateChatRequest;
import com.imbus.knowledge.chat.dto.UserInfoDto;
import com.imbus.knowledge.chat.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public ResponseEntity<ChatDto> createChat(@RequestBody CreateChatRequest request,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        ChatDto chat = chatService.createChat(request.getParticipantIds(), false, null, userId);
        return ResponseEntity.ok(chat);
    }

    @PostMapping("/group")
    public ResponseEntity<ChatDto> createGroupChat(@RequestBody CreateChatRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required");
        }
        Long userId = userDetails.getUser().getId();
        ChatDto chat = chatService.createChat(request.getParticipantIds(), true, request.getName(), userId);
        return ResponseEntity.ok(chat);
    }

}