package com.imbus.knowledge.Content_Management.services;

import com.imbus.knowledge.Content_Management.dto.UserStats;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.repositories.FavoriteRepository;
import com.imbus.knowledge.Content_Management.repositories.PostRepository;
import com.imbus.knowledge.Content_Management.repositories.ShareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final PostRepository postRepository;
    private final FavoriteRepository favoriteRepository;
    private final ShareRepository shareRepository;

    public UserStats getUserStats(Long userId) {
        List<Post> userPosts = postRepository.findByAuthorId(userId);

        int articlesPublished = userPosts.size();
        int totalViews = userPosts.stream().mapToInt(Post::getViewCount).sum();
        int favorites = favoriteRepository.countByUserId(userId);
        int shares = shareRepository.countByUserId(userId);

        List<String> recentActivity = new ArrayList<>();
        // You can fetch recent actions from repositories if needed
        recentActivity.add("Published article on Vue 3");
        recentActivity.add("Reacted 👍 to TypeScript Guide");

        return UserStats.builder()
                .articlesPublished(articlesPublished)
                .totalViews(formatLargeNumber(totalViews))
                .contributions(articlesPublished + favorites + shares)
                .reviewsCount(favorites)
                .sharesCount(shares)
                .recentActivity(recentActivity)
                .build();
    }

    private String formatLargeNumber(int count) {
        if (count < 1000) return String.valueOf(count);
        double thousands = Math.round(count / 100.0) / 10.0;
        return String.format("%.1fK", thousands);
    }
}
