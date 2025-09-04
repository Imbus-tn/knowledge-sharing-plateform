package com.imbus.knowledge.Content_Management.services;

import com.imbus.knowledge.Content_Management.dto.*;
import com.imbus.knowledge.Content_Management.entities.*;
import com.imbus.knowledge.Content_Management.exception.PostNotFoundException;
import com.imbus.knowledge.Content_Management.repositories.*;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.entities.UserRole;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;
    private final PostReactionRepository reactionRepository;
    private final ReportedPostRepository reportedPostRepository;
    private final ShareRepository shareRepository;

    // ===== POST CRUD =====



    public Post getPostById(Long id) {
        return postRepository.findWithDetailsById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // PostService.java
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
    public Map<String, Long> getReactionCounts(Long postId) {
        return reactionRepository.countByPostIdGroupedByEmoji(postId).stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],  // emoji
                        arr -> (Long) arr[1]    // count
                ));
    }


    // Add this method to PostService.java
    public Post updatePost(Long postId, CreatePostRequest request, Long userId) {
        Post post = getPostById(postId);

        if (!post.getAuthor().getId().equals(userId) && !isUserAdmin(userId)) {
            throw new SecurityException("You are not authorized to update this post.");
        }

        // Update fields
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setCategory(request.getCategory());
        post.setTags(request.getTags());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public Post updatePostImage(Long postId, String imageUrl, Long userId) {
        Post post = getPostById(postId);

        if (!post.getAuthor().getId().equals(userId) && !isUserAdmin(userId)) {
            throw new SecurityException("You are not authorized to update this post.");
        }

        post.setImageUrl(imageUrl);
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }
    public List<PostResponse> getLatestPosts(Instant since, int limit, UserDetails userDetails) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Post> postPage;

        if (since != null) {
            postPage = postRepository.findLatestPosts(since, pageable);
        } else {
            postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        List<Post> posts = postPage.getContent(); // ✅ Extract List from Page
        if (posts.isEmpty()) {
            return List.of();
        }

        // Get current user for favorite check
        User user = null;
        if (userDetails != null) {
            user = userRepository.findById(((UserDetailsImpl) userDetails).getUser().getId()).orElse(null);
        }

        // Get favorite status for all posts in batch
        Set<Long> favoritePostIds = new HashSet<>();
        if (user != null) {
            favoritePostIds.addAll(favoriteRepository.findPostIdsByUser(user.getId()));
        }

        // Map to PostResponse
        return posts.stream().map(post -> {
            boolean isFavorite = favoritePostIds.contains(post.getId());
            return PostResponse.fromEntity(post, isFavorite);
        }).collect(Collectors.toList());
    }

    public void deletePost(Long postId, Long userId) {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        if (!isUserAdmin(userId) && !post.getAuthor().getId().equals(userId)) {
            throw new SecurityException("You can only delete your own posts.");
        }

        postRepository.delete(post);
    }

    // Helper: Check if user is admin
    private boolean isUserAdmin(Long userId) {
        User user = getUserById(userId);
        return user.getRole() == UserRole.ADMIN;
    }

    // ===== INTERACTIONS (All users can do these) =====

    @Transactional
    public void toggleFavorite(Long postId, Long userId) {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        Optional<Favorite> existing = favoriteRepository.findByUserAndPost(user, post);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setPost(post);
            favorite.setCreatedAt(LocalDateTime.now());
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void reactToPost(Long postId, ReactionRequest request, Long userId) {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        PostReaction existing = reactionRepository.findByUserAndPost(user, post);

        if (request.getType() != null && existing == null) {
            // New like
            post.setLikeCount(post.getLikeCount() + 1);
            PostReaction reaction = new PostReaction();
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setEmoji(request.getType());
            reaction.setCreatedAt(LocalDateTime.now());
            reactionRepository.save(reaction);
        } else if (request.getType() == null && existing != null) {
            // Remove like
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            reactionRepository.delete(existing);
        }
        postRepository.save(post);
    }


    public Comment replyToComment(Long commentId, CommentRequest request, Long userId) {
        Comment parent = getCommentById(commentId);
        User user = getUserById(userId);

        Comment reply = new Comment();
        reply.setText(request.getText());
        reply.setPost(parent.getPost());
        reply.setAuthor(user);
        reply.setParent(parent);
        reply.setCreatedAt(LocalDateTime.now());

        Comment savedReply = commentRepository.save(reply);

        // ✅ Add to parent's replies list
        if (parent.getReplies() == null) {
            parent.setReplies(new ArrayList<>());
        }
        parent.getReplies().add(savedReply);

        return savedReply;
    }

    public void reactToComment(Long commentId, ReactionRequest request, Long userId) {
        Comment comment = getCommentById(commentId);
        User user = getUserById(userId);

        PostReaction existingPostReaction = reactionRepository.findByUserAndComment(user, comment);

        if (existingPostReaction != null) {
            existingPostReaction.setEmoji(request.getType());
            reactionRepository.save(existingPostReaction);
        } else {
            PostReaction postReaction = new PostReaction();
            postReaction.setUser(user);
            postReaction.setComment(comment);
            postReaction.setEmoji(request.getType());
            postReaction.setCreatedAt(LocalDateTime.now());
            reactionRepository.save(postReaction);
        }
    }

    public void reportPost(Long postId, ReportRequest request, User reporter) {
        Post post = getPostById(postId);

        ReportedPost reportedPost = new ReportedPost();
        reportedPost.setPost(post);
        reportedPost.setReporter(reporter);
        reportedPost.setReason(request.getReason());
        reportedPost.setReportedAt(LocalDateTime.now());

        reportedPostRepository.save(reportedPost);
    }
    @Transactional
    public void removeFavorite(Long postId, Long userId) {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        Optional<Favorite> existing = favoriteRepository.findByUserAndPost(user, post);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
        }
    }
    public void sharePost(Long postId, Long userId) {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        Share share = new Share();
        share.setPost(post);
        share.setUser(user);
        share.setSharedAt(LocalDateTime.now());
        shareRepository.save(share);

        // Increment share count
        post.setShareCount(post.getShareCount() + 1);
        postRepository.save(post);
    }
    public Post createPost(CreatePostRequest request, Long userId) {
        User author = getUserById(userId);

        if (author.getRole() != UserRole.ADMIN && author.getRole() != UserRole.CONTRIBUTOR) {
            throw new SecurityException("Only Contributor or Admin can create posts.");
        }

        Post post = new Post();
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setCategory(request.getCategory());
        post.setTags(request.getTags());
        post.setLinkUrl(request.getLinkUrl());
        post.setLinkPreview(LinkPreviewDto.fromDto(request.getLinkPreview()));
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    @Transactional
    public void incrementViewCount(Long postId) {
        postRepository.incrementViewCount(postId);
    }
    // ===== HELPERS =====

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }
    @Transactional
    public Comment addCommentToPost(Long postId, CommentRequest request, Long userId) {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment); // ✅ Return saved comment
    }

}