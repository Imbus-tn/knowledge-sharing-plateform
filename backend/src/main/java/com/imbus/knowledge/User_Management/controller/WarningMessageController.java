package com.imbus.knowledge.User_Management.controller;

import com.imbus.knowledge.User_Management.dto.SendWarningRequest;
import com.imbus.knowledge.User_Management.dto.WarningMessageDto;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.entities.WarningMessage;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.User_Management.repositories.WarningMessageRepository;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warnings")
@RequiredArgsConstructor
public class WarningMessageController {

    private final WarningMessageRepository warningRepo;
    private final UserRepository userRepo;

    // ✅ Admin: Send a warning
    @PostMapping("/user/{userId}")
    public ResponseEntity<WarningMessageDto> sendWarning(
            @PathVariable Long userId,
            @RequestBody SendWarningRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User admin = userDetails.getUser();

        WarningMessage warning = new WarningMessage();
        warning.setUser(user);
        warning.setAdmin(admin);
        warning.setMessage(request.message());
        warning.setCreatedAt(LocalDateTime.now());

        warningRepo.save(warning);

        return ResponseEntity.ok(toDto(warning));
    }

    // ✅ User: View their warnings
    @GetMapping("/my")
    public ResponseEntity<List<WarningMessageDto>> getMyWarnings(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        List<WarningMessage> warnings = warningRepo.findByUserIdOrderByCreatedAtDesc(user.getId());

        return ResponseEntity.ok(warnings.stream().map(this::toDto).collect(Collectors.toList()));
    }

    // ✅ Admin: View all unresolved warnings
    @GetMapping("/unresolved")
    public ResponseEntity<List<WarningMessageDto>> getUnresolvedWarnings(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (!isAdmin(userDetails)) {
            return ResponseEntity.status(403).build();
        }

        List<WarningMessage> warnings = warningRepo.findByResolvedFalse();
        return ResponseEntity.ok(warnings.stream().map(this::toDto).collect(Collectors.toList()));
    }

    // ✅ Admin: Mark warning as resolved
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveWarning(@PathVariable Long id) {
        WarningMessage warning = warningRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Warning not found"));
        warning.setResolved(true);
        warningRepo.save(warning);
        return ResponseEntity.noContent().build();
    }

    // Helper: Convert to DTO
    private WarningMessageDto toDto(WarningMessage w) {
        return new WarningMessageDto(
                w.getId(),
                w.getMessage(),
                w.getAdmin().getName(),
                w.getCreatedAt(),
                w.isResolved()
        );
    }

    private boolean isAdmin(UserDetailsImpl userDetails) {
        return "ADMIN".equals(userDetails.getUser().getRole().name());
    }
}

// DTO for request
