package com.imbus.knowledge.chat.services;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.chat.dto.*;
import com.imbus.knowledge.chat.entities.Chat;
import com.imbus.knowledge.chat.exception.ChatNotFoundException;
import com.imbus.knowledge.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.messaging.simp.SimpMessagingTemplate;
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserPresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;



    @Transactional(readOnly = true)
    public List<ChatDto> getUserChats(Long userId) {
        return chatRepository.findByParticipants_Id(userId).stream()
                .map(this::convertToDto)
                .sorted((c1, c2) -> c2.getLastActivity().compareTo(c1.getLastActivity()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatDto getChatById(Long chatId, Long userId) {
        if (!chatRepository.existsByIdAndParticipants_Id(chatId, userId)) {
            throw new ChatNotFoundException("Access denied");
        }

        return chatRepository.findById(chatId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
    }

    private ChatDto convertToDto(Chat chat) {
        return ChatDto.builder()
                .id(chat.getId())
                .name(chat.getName())
                .isGroup(chat.isGroup())
                .createdAt(chat.getCreatedAt())
                .lastActivity(chat.getLastActivity())
                .participants(chat.getParticipants().stream()
                        .map(this::convertToUserDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    private UserInfoDto convertToUserDto(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .online(presenceService.isUserOnline(user.getId()))
                .build();
    }
    @Transactional
    public ChatDto createChat(List<Long> participantIds, boolean isGroup, String groupName, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Set<User> participants = new HashSet<>(userRepository.findAllById(participantIds));
        participants.add(creator); // Always include creator

        if (!isGroup && participants.size() != 2) {
            throw new IllegalArgumentException("Direct chat must have exactly one other participant");
        }

        Chat chat = new Chat();
        chat.setGroup(isGroup);
        chat.setParticipants(participants);
        chat.setCreatedAt(LocalDateTime.now());
        chat.setLastActivity(LocalDateTime.now());

        if (isGroup) {
            chat.setName(groupName);
        } else {
            // For 1:1, name = other user's name
            User other = participants.stream()
                    .filter(p -> !p.getId().equals(creatorId))
                    .findFirst()
                    .map(p -> p)
                    .orElseThrow();
            chat.setName(other.getName());
        }

        Chat savedChat = chatRepository.save(chat);

        // Notify participants via WebSocket
        messagingTemplate.convertAndSendToUser(
                creatorId.toString(),
                "/queue/chats",
                Map.of("action", "created", "chatId", savedChat.getId())
        );

        return convertToDto(savedChat);
    }
}