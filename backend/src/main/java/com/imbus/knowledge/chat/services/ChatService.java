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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserPresenceService presenceService;

    @Transactional
    public ChatDto createChat(Set<Long> participantIds, String chatName, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Set<User> participants = new HashSet<>(userRepository.findAllById(participantIds));
        participants.add(creator); // Include creator in participants

        Chat chat = new Chat();
        chat.setParticipants(participants);
        chat.setCreatedAt(LocalDateTime.now());
        chat.setLastActivity(LocalDateTime.now());

        if (participants.size() > 2) {
            chat.setGroup(true);
            chat.setName(chatName != null ? chatName : "Group Chat");
        } else {
            chat.setGroup(false);
            // Set chat name as the other participant's name for 1:1 chats
            String otherParticipantName = participants.stream()
                    .filter(p -> !p.getId().equals(creatorId))
                    .findFirst()
                    .map(User::getName)
                    .orElse("Private Chat");
            chat.setName(otherParticipantName);
        }

        return convertToDto(chatRepository.save(chat));
    }

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
}