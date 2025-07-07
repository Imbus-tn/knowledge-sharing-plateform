package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.UserStats;
import com.imbus.knowledge.Content_Management.services.StatsService;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/me/stats")
    public ResponseEntity<UserStats> getCurrentUserStats(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        UserStats stats = statsService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }
}
