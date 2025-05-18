package com.imbus.knowledge.chat.services;



import com.imbus.knowledge.chat.dto.*;
import com.imbus.knowledge.chat.entities.Chat;
import com.imbus.knowledge.chat.entities.Message;
import com.imbus.knowledge.chat.entities.User;
import com.imbus.knowledge.chat.exception.ChatNotFoundException;
import com.imbus.knowledge.chat.exception.MessageNotFoundException;
import com.imbus.knowledge.chat.repository.ChatRepository;
import com.imbus.knowledge.chat.repository.MessageRepository;
import com.imbus.knowledge.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageDto sendMessage(Long chatId, Long senderId, String content, Long replyToId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        if (replyToId != null) {
            Message replyTo = messageRepository.findById(replyToId)
                    .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + replyToId));
            message.setReplyTo(replyTo);
        }

        chat.setLastActivity(LocalDateTime.now());
        chatRepository.save(chat);

        return convertToDto(messageRepository.save(message));
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getChatMessages(Long chatId, Long userId) {
        if (!chatRepository.existsByIdAndParticipantsId(chatId, userId)) {
            throw new ChatNotFoundException("Chat not found or user not a participant");
        }

        return messageRepository.findByChatIdOrderBySentAtAsc(chatId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(Long chatId, Long userId) {
        List<Message> unreadMessages = messageRepository.findUnreadMessages(chatId, userId);
        unreadMessages.forEach(m -> m.setRead(true));
        messageRepository.saveAll(unreadMessages);
    }

    private MessageDto convertToDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .sender(convertToUserDto(message.getSender()))
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .read(message.isRead())
                .replyTo(message.getReplyTo() == null ? null : convertToMessagePreviewDto(message.getReplyTo()))
                .attachment(message.getAttachment() == null ? null :
                        AttachmentDto.builder()
                                .type(message.getAttachment().getType())
                                .url(message.getAttachment().getUrl())
                                .name(message.getAttachment().getName())
                                .size(message.getAttachment().getSize())
                                .build())
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