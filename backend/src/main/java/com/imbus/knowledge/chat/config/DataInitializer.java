package com.imbus.knowledge.chat.config;



import com.imbus.knowledge.chat.entities.*;
import com.imbus.knowledge.chat.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Profile("!prod")
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepo,
            ChatRepository chatRepo,
            MessageRepository msgRepo,
            ReactionRepository reactRepo) {

        return args -> {
            if (userRepo.count() == 0) {
                // Create users
                User alice = User.builder()
                        .username("alice")
                        .password(passwordEncoder.encode("password123"))
                        .name("Alice")
                        .online(true)
                        .build();

                User bob = User.builder()
                        .username("bob")
                        .password(passwordEncoder.encode("password123"))
                        .name("Bob")
                        .online(false)
                        .build();

                User charlie = User.builder()
                        .username("charlie")
                        .password(passwordEncoder.encode("password123"))
                        .name("Charlie")
                        .online(true)
                        .build();

                userRepo.saveAll(Arrays.asList(alice, bob, charlie));

                // Create chats
                Chat directChat = Chat.builder()
                        .isGroup(false)
                        .createdAt(LocalDateTime.now())
                        .lastActivity(LocalDateTime.now())
                        .build();
                directChat.getParticipants().add(alice);
                directChat.getParticipants().add(bob);

                Chat groupChat = Chat.builder()
                        .name("Developers Group")
                        .isGroup(true)
                        .createdAt(LocalDateTime.now())
                        .lastActivity(LocalDateTime.now())
                        .build();
                groupChat.getParticipants().addAll(Arrays.asList(alice, bob, charlie));

                chatRepo.saveAll(Arrays.asList(directChat, groupChat));

                // Create messages
                Message msg1 = Message.builder()
                        .chat(directChat)
                        .sender(alice)
                        .content("Hi Bob!")
                        .sentAt(LocalDateTime.now())
                        .read(false)
                        .build();

                Message msg2 = Message.builder()
                        .chat(directChat)
                        .sender(bob)
                        .content("Hello Alice!")
                        .sentAt(LocalDateTime.now())
                        .read(true)
                        .build();

                Message groupMsg = Message.builder()
                        .chat(groupChat)
                        .sender(charlie)
                        .content("Let's discuss the project")
                        .sentAt(LocalDateTime.now())
                        .read(false)
                        .attachment(Attachment.builder()
                                .type("document")
                                .name("Project.pdf")
                                .size(2048L)
                                .build())
                        .build();

                msgRepo.saveAll(Arrays.asList(msg1, msg2, groupMsg));

                // Create reactions
                Reaction like = Reaction.builder()
                        .message(msg1)
                        .user(bob)
                        .emoji("👍")
                        .build();

                Reaction heart = Reaction.builder()
                        .message(groupMsg)
                        .user(alice)
                        .emoji("❤️")
                        .build();

                reactRepo.saveAll(Arrays.asList(like, heart));
            }
        };
    }
}