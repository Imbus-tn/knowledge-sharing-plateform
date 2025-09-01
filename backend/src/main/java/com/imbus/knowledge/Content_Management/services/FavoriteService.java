package com.imbus.knowledge.Content_Management.services;

import com.imbus.knowledge.Content_Management.dto.FavoritePostResponse;
import com.imbus.knowledge.Content_Management.entities.Favorite;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.repositories.FavoriteRepository;
import com.imbus.knowledge.Content_Management.repositories.PostRepository;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.imbus.knowledge.Content_Management.dto.AuthorDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public List<FavoritePostResponse> getFavoritesByUser(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(fav -> mapToFavoritePostResponse(fav.getPost(), fav.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void addFavorite(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (favoriteRepository.existsByUserAndPost(user, post)) {
            return; // Already favorited
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setPost(post);
        favorite.setCreatedAt(LocalDateTime.now());

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<Favorite> existing = favoriteRepository.findByUserAndPost(user, post);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
        }
    }

    private FavoritePostResponse mapToFavoritePostResponse(Post post, LocalDateTime favoritedAt) {
        return FavoritePostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .coverImage(post.getImageUrl())
                .author(AuthorDto.fromEntity(post.getAuthor()))
                .createdAt(post.getCreatedAt())
                .likes(post.getLikeCount())
                .comments(post.getComments().size())
                .shares(post.getShareCount())
                .isFavorite(true)
                .build();
    }
}