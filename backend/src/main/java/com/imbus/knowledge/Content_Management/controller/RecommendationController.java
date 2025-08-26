package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${recommender.content.url:http://localhost:5000}")
    private String contentRecommenderUrl;

    @Value("${recommender.feed.url:http://localhost:5001}")
    private String feedRecommenderUrl;

    // Search with AI
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String title,
            @RequestParam(defaultValue = "5") int top_n) {

        return forwardGet(contentRecommenderUrl + "/recommend", Map.of("title", title, "top_n", top_n));
    }

    // Personalized feed
    @GetMapping("/feed")
    public ResponseEntity<?> getFeed(
            @RequestParam(defaultValue = "10") int num_recommendations,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        String userId = String.valueOf(userDetails.getUser().getId());
        return forwardGet(feedRecommenderUrl + "/recommend", Map.of("user_id", userId, "num_recommendations", num_recommendations));
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try {
            ResponseEntity<String> content = restTemplate.getForEntity(contentRecommenderUrl + "/health", String.class);
            ResponseEntity<String> feed = restTemplate.getForEntity(feedRecommenderUrl + "/health", String.class);
            return ResponseEntity.ok(Map.of(
                    "content_recommender", content.getBody(),
                    "feed_recommender", feed.getBody()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(503).body("Service unreachable");
        }
    }

    private ResponseEntity<?> forwardGet(String url, Map<String, Object> params) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            params.forEach(builder::queryParam);
            ResponseEntity<Map> response = restTemplate.getForEntity(builder.toUriString(), Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of("error", "Service unavailable"));
        }
    }
}