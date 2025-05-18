package com.imbus.knowledge.chat.controller;



import com.imbus.knowledge.chat.dto.ReactionDto;
import com.imbus.knowledge.chat.services.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping
    public ResponseEntity<ReactionDto> addReaction(
            @RequestParam Long messageId,
            @RequestParam Long userId,
            @RequestParam String emoji) {
        return ResponseEntity.ok(reactionService.addReaction(messageId, userId, emoji));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeReaction(
            @RequestParam Long messageId,
            @RequestParam Long userId) {
        reactionService.removeReaction(messageId, userId);
        return ResponseEntity.noContent().build();
    }
}