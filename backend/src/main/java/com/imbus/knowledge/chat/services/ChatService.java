package com.imbus.knowledge.chat.services;

import com.imbus.knowledge.chat.dto.*;
import com.imbus.knowledge.chat.entities.*;
import com.imbus.knowledge.chat.exception.ChatNotFoundException;
import com.imbus.knowledge.chat.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public ChatDto createChat(Set<Long> participantIds, String chatName) {
        Chat chat = new Chat();
        chat.setParticipants(userRepository.findAllByIdIn(participantIds));
        chat.setCreatedAt(LocalDateTime.now());
        chat.setLastActivity(LocalDateTime.now());

        if (participantIds.size() > 2) {
            chat.setGroup(true);
            chat.setName(chatName);
        }

        return convertToDto(chatRepository.save(chat));
    }

    @Transactional(readOnly = true)
    public List<ChatDto> getUserChats(Long userId) {
        return chatRepository.findAllByParticipantId(userId).stream()
                .map(this::convertToDtoWithUnreadCount)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatDto getChatById(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

        if (!chat.getParticipants().stream().anyMatch(p -> p.getId().equals(userId))) {
            throw new ChatNotFoundException("User is not a participant of this chat");
        }

        return convertToDtoWithUnreadCount(chat);
    }

    private ChatDto convertToDtoWithUnreadCount(Chat chat) {
        ChatDto dto = convertToDto(chat);
        dto.setUnreadCount(messageRepository.countUnreadMessages(chat.getId(),
                chat.getParticipants().iterator().next().getId()));
        return dto;
    }

    private ChatDto convertToDto(Chat chat) {
        return ChatDto.builder()
                .id(chat.getId())
                .name(chat.getName())
                .isGroup(chat.isGroup())
                .participants(chat.getParticipants().stream()
                        .map(this::convertToParticipantDto)
                        .collect(Collectors.toSet()))
                .createdAt(chat.getCreatedAt())
                .lastActivity(chat.getLastActivity())
                .lastMessage(chat.getMessages().isEmpty() ? null :
                        convertToMessagePreviewDto(chat.getMessages().iterator().next()))
                .build();
    }

    private ParticipantDto convertToParticipantDto(User user) {
        return ParticipantDto.builder()
                .id(user.getId())
                .name(user.getName())
                .initials(getInitials(user.getName()))
                .avatarUrl(user.getAvatarUrl())
                .online(user.isOnline())
                .build();
    }

    private MessagePreviewDto convertToMessagePreviewDto(Message message) {
        return MessagePreviewDto.builder()
                .id(message.getId())
                .preview(message.getContent().length() > 30 ?
                        message.getContent().substring(0, 30) + "..." : message.getContent())
                .sentAt(message.getSentAt())
                .sender(convertToUserDto(message.getSender()))
                .build();
    }

    private UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .initials(getInitials(user.getName()))
                .avatarUrl(user.getAvatarUrl())
                .online(user.isOnline())
                .build();
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "";
        String[] parts = name.split(" ");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}