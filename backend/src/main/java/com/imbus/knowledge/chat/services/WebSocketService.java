package com.imbus.knowledge.chat.services;



import com.imbus.knowledge.chat.dto.MessageDto;
import com.imbus.knowledge.chat.entities.Chat;
import com.imbus.knowledge.chat.entities.User;
import com.imbus.knowledge.chat.repository.ChatRepository;
import com.imbus.knowledge.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public void notifyNewMessage(MessageDto messageDto) {
        messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getChatId(), messageDto);

        // Notify participants about new message (except sender)
        Chat chat = chatRepository.findById(messageDto.getChatId()).orElseThrow();
        chat.getParticipants().stream()
                .filter(p -> !p.getId().equals(messageDto.getSender().getId()))
                .forEach(p -> {
                    messagingTemplate.convertAndSendToUser(
                            p.getUsername(),
                            "/queue/notifications",
                            "New message in chat " + chat.getName());
                });
    }

    public void notifyUserStatus(Long userId, boolean isOnline) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setOnline(isOnline);
        userRepository.save(user);

        // Notify all chats where this user is a participant
        List<Chat> chats = chatRepository.findAllByParticipantId(userId);
        chats.forEach(chat -> {
            messagingTemplate.convertAndSend("/topic/chat/" + chat.getId() + "/presence",
                    new UserPresence(userId, isOnline));
        });
    }

    public record UserPresence(Long userId, boolean isOnline) {}
}