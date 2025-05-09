package com.imbus.knowledge.User_Management.services;

import com.imbus.knowledge.User_Management.entities.RefreshToken;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.RefreshTokenRepository;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + username));

        // Delete existing refresh token if it exists
        refreshTokenRepository.deleteByUser(user);

        long refreshTokenValidity = 5 * 60 * 60 * 1000; // 5 hours
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found!"));

        System.out.println("Current Time: " + Instant.now());
        System.out.println("Token Expiration Time: " + refToken.getExpirationTime());

        if (refToken.getExpirationTime().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refToken);
            throw new RuntimeException("Refresh Token expired");
        }

        return refToken;
    }
}
