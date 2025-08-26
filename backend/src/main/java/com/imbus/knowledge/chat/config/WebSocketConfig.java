package com.imbus.knowledge.chat.config;

import com.imbus.knowledge.User_Management.services.JwtService;
import com.imbus.knowledge.User_Management.services.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

import static com.imbus.knowledge.User_Management.controller.ForgotPasswordController.logger;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor  // ✅ Add this
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final UserDetailsServiceImpl userDetailsServiceImp;  // ✅ Field name must match variable
    private final JwtService jwtService;  // ✅ Injected

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> auth = accessor.getNativeHeader("Authorization");
                    if (auth != null && !auth.isEmpty()) {
                        String authToken = auth.get(0);
                        if (authToken.startsWith("Bearer ")) {
                            String token = authToken.substring(7);
                            try {
                                String username = extractUsernameFromJwt(token);
                                UserDetails userDetails = userDetailsServiceImp.loadUserByUsername(username);
                                if (jwtService.isTokenValid(token, userDetails)) {
                                    Authentication authResult = new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                                    accessor.setUser(authResult);
                                }
                            } catch (Exception e) {
                                logger.warn("Invalid JWT in WebSocket", e);
                            }
                        }
                    }
                }
                return message;
            }
        });
    }

    private String extractUsernameFromJwt(String token) {
        return Jwts.parser()
                .setSigningKey("gW0I6cnjCGXWp0qSiu+92ZTY8IwzUlCoYXpCFK696h8=")
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}