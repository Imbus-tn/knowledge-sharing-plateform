package com.imbus.knowledge.User_Management.controller;

import com.imbus.knowledge.User_Management.dto.*;
import com.imbus.knowledge.User_Management.entities.Invitation;
import com.imbus.knowledge.User_Management.exception.ValidationException;
import com.imbus.knowledge.User_Management.repositories.InvitationRepository;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import com.imbus.knowledge.User_Management.entities.RefreshToken;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.services.AuthService;
import com.imbus.knowledge.User_Management.services.JwtService;
import com.imbus.knowledge.User_Management.services.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final InvitationRepository invitationRepository;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService, InvitationRepository invitationRepository) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.invitationRepository = invitationRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String token , @RequestBody RegisterRequest registerRequest) {

        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation"));

        if (invitation.isUsed()) {
            return ResponseEntity.badRequest().body(Map.of("general", List.of("Invitation already used")));
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())){
            return ResponseEntity.badRequest().body(Map.of(
                    "general", List.of("Invitation expired")));
        }

        try {
            // Attempt registration
            authService.register(
                    invitation.getEmail(),
                    invitation.getRole(),
                    registerRequest);

            // Mark invitation as used only on SUCCESS
            invitation.setUsed(true);
            invitationRepository.save(invitation);

            return ResponseEntity.ok(Map.of("success", "Registration complete. Please sign in."));
        }catch (ValidationException e){
            return ResponseEntity.badRequest().body(e.getErrors());
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of(
                    "general", List.of(e.getMessage())
            ));
        }

    }

    @GetMapping("/invitation/validate")
    public ResponseEntity<InvitationResponse> validateToken(@RequestParam String token){
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));
        if (invitation.isUsed()) {
            throw new RuntimeException("Invitation already used");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invitation expired");
        }

        return ResponseEntity.ok(InvitationResponse.builder()
                .email(invitation.getEmail())
                .role(invitation.getRole())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "general", List.of("The email or password you entered is incorrect")
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "general", List.of(e.getMessage())
            ));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
            User user = refreshToken.getUser();

            String accessToken = jwtService.generateToken(user);

            return ResponseEntity.ok(AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getRefreshToken())
                    .build());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("expired")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "Refresh token expired"
                ));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Invalid refresh token"
            ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails){
        if (userDetails == null) {
            throw new RuntimeException("User not authenticated");
        }
        User user = userDetails.user();
        UserResponse response = UserResponse.fromEntity(user);
        return ResponseEntity.ok(response);
    }
}
