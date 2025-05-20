package com.imbus.knowledge.chat.services;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.chat.dto.ReactionDto;
import com.imbus.knowledge.chat.dto.UserInfoDto;
import com.imbus.knowledge.chat.entities.*;
import com.imbus.knowledge.chat.exception.ReactionNotFoundException;
import com.imbus.knowledge.chat.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public ReactionDto addReaction(Long messageId, Long userId, String emoji) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!chatRepository.existsByIdAndParticipantsId(message.getChat().getId(), userId)) {
            throw new RuntimeException("Unauthorized reaction");
        }

        reactionRepository.deleteByMessageIdAndUserId(messageId, userId);

        Reaction reaction = new Reaction();
        reaction.setMessage(message);
        reaction.setUser(user);
        reaction.setEmoji(emoji);

        return convertToDto(reactionRepository.save(reaction));
    }

    @Transactional
    public void removeReaction(Long messageId, Long userId) {
        if (!reactionRepository.existsByMessageIdAndUserId(messageId, userId)) {
            throw new ReactionNotFoundException();
        }
        reactionRepository.deleteByMessageIdAndUserId(messageId, userId);
    }

    private ReactionDto convertToDto(Reaction reaction) {
        return ReactionDto.builder()
                .id(reaction.getId())
                .emoji(reaction.getEmoji())
                .user(convertToUserDto(reaction.getUser()))
                .build();
    }

    private UserInfoDto convertToUserDto(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}