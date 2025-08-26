package com.imbus.knowledge.chat.controller;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.chat.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/search")
    public ResponseEntity<List<UserInfoDto>> searchUsers(@RequestParam String q) {
        try {
            if (q == null || q.trim().isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<User> users = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q.trim(), q.trim());

            List<UserInfoDto> dtos = users.stream()
                    .filter(Objects::nonNull)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }


    private UserInfoDto convertToDto(User user) {
        if (user == null) return null;
        return UserInfoDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .online(false) // Will be updated by presence service later
                .build();
    }
}