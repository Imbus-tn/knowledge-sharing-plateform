package com.imbus.knowledge.chat.controller;


import com.imbus.knowledge.chat.dto.UserPresenceDto;
import com.imbus.knowledge.chat.services.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
public class PresenceController {
    private final PresenceService presenceService;

    @GetMapping("/{userId}")
    public UserPresenceDto getPresence(@PathVariable Long userId) {
        return presenceService.getUserPresence(userId);
    }

    @PostMapping("/connect/{userId}")
    public void userConnected(@PathVariable Long userId) {
        presenceService.userConnected(userId);
    }

    @PostMapping("/disconnect/{userId}")
    public void userDisconnected(@PathVariable Long userId) {
        presenceService.userDisconnected(userId);
    }
}