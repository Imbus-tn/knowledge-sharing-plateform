package com.imbus.knowledge.chat.services;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.chat.dto.*;
import com.imbus.knowledge.chat.entities.*;
import com.imbus.knowledge.chat.exception.*;
import com.imbus.knowledge.chat.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final UserPresenceService presenceService;

    @Transactional
    public MessageDto sendMessage(Long chatId, Long senderId, String content, Long replyToId, MultipartFile file) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

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
                    .orElseThrow(() -> new MessageNotFoundException("Reply message not found"));
            message.setReplyToMessage(replyTo);
        }

        if (file != null && !file.isEmpty()) {
            try {
                FileUploadResponse fileResponse = fileStorageService.storeFile(file);
                Attachment attachment = new Attachment();
                attachment.setType(fileResponse.getFileType());
                attachment.setUrl(fileResponse.getFileDownloadUri());
                attachment.setName(fileResponse.getFileName());
                attachment.setSize(fileResponse.getSize());
                message.setAttachment(attachment);
            } catch (FileUploadException e) {
                throw new RuntimeException("Failed to upload file: " + e.getMessage());
            }
        }

        chat.setLastActivity(LocalDateTime.now());
        chatRepository.save(chat);

        return convertToDto(messageRepository.save(message));
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getChatMessages(Long chatId, Long userId) {
        if (!chatRepository.existsByIdAndParticipantsId(chatId, userId)) {
            throw new ChatNotFoundException("Access denied");
        }

        return messageRepository.findByChatIdOrderBySentAtDesc(chatId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(Long chatId, Long userId) {
        messageRepository.updateReadStatus(chatId, userId, true);
    }

    private MessageDto convertToDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .sender(convertToUserDto(message.getSender()))
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .read(message.isRead())
                .replyTo(message.getReplyToMessage() == null ? null :
                        MessagePreviewDto.builder()
                                .id(message.getReplyToMessage().getId())
                                .preview(getMessagePreview(message.getReplyToMessage().getContent()))
                                .sentAt(message.getReplyToMessage().getSentAt())
                                .sender(convertToUserDto(message.getReplyToMessage().getSender()))
                                .build())
                .attachment(message.getAttachment() == null ? null :
                        AttachmentDto.builder()
                                .type(message.getAttachment().getType())
                                .url(message.getAttachment().getUrl())
                                .name(message.getAttachment().getName())
                                .size(message.getAttachment().getSize())
                                .build())
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

    private String getMessagePreview(String content) {
        return content.length() > 30 ? content.substring(0, 30) + "..." : content;
    }
}