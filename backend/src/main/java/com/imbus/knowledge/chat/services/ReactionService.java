package com.imbus.knowledge.chat.services;

import com.imbus.knowledge.chat.dto.*;
import com.imbus.knowledge.chat.entities.*;
import com.imbus.knowledge.chat.exception.*;
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
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!chatRepository.existsByIdAndParticipantsId(message.getChat().getId(), userId)) {
            throw new UnauthorizedReactionException();
        }

        // Remove existing reaction if exists
        reactionRepository.deleteByMessageIdAndUserId(messageId, userId);

        Reaction reaction = new Reaction();
        reaction.setMessage(message);
        reaction.setUser(user);
        reaction.setEmoji(emoji);

        Reaction savedReaction = reactionRepository.save(reaction);
        return convertToDto(savedReaction);
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