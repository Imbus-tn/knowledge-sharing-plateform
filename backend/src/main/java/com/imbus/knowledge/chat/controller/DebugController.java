package com.imbus.knowledge.chat.controller;



import com.imbus.knowledge.chat.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final UserRepository userRepo;
    private final ChatRepository chatRepo;
    private final MessageRepository msgRepo;
    private final ReactionRepository reactRepo;

    public DebugController(UserRepository userRepo, ChatRepository chatRepo,
                           MessageRepository msgRepo, ReactionRepository reactRepo) {
        this.userRepo = userRepo;
        this.chatRepo = chatRepo;
        this.msgRepo = msgRepo;
        this.reactRepo = reactRepo;
    }

    @GetMapping("/status")
    public String getDatabaseStatus() {
        return String.format(
                "Users: %d, Chats: %d, Messages: %d, Reactions: %d",
                userRepo.count(),
                chatRepo.count(),
                msgRepo.count(),
                reactRepo.count()
        );
    }
}