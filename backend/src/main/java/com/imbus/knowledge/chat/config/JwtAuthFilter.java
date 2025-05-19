package com.imbus.knowledge.chat.config;

import com.imbus.knowledge.User_Management.services.JwtService;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userRepository.findByEmail(userEmail)
                    .ifPresent(user -> {
                        if (jwtService.isTokenValid(jwt, user)) {
                            var authToken = new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities()
                            );
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    });
        }

        filterChain.doFilter(request, response);
    }
}