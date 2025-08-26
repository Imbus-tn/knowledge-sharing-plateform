package com.imbus.knowledge.Notification.controller;

import com.imbus.knowledge.Notification.service.NotificationService;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        var notifications = notificationService.getUserNotifications(userId, page, size);
        long unreadCount = notificationService.getUnreadCount(userId);

        return ResponseEntity.ok(Map.of(
                "notifications", notifications,
                "unreadCount", unreadCount
        ));
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/mark-read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-new")
    public ResponseEntity<Boolean> checkForNewNotifications(
            @RequestParam("lastChecked") LocalDateTime lastChecked,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        boolean hasNew = notificationService.hasNewNotifications(userId, lastChecked);
        return ResponseEntity.ok(hasNew);
    }
}