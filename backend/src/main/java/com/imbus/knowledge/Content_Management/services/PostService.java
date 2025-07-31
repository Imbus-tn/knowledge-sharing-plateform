package com.imbus.knowledge.Content_Management.services;

import com.imbus.knowledge.Content_Management.dto.CreatePostRequest;
import com.imbus.knowledge.Content_Management.dto.CommentRequest;
import com.imbus.knowledge.Content_Management.dto.ReactionRequest;
import com.imbus.knowledge.Content_Management.dto.ReportRequest;
import com.imbus.knowledge.Content_Management.entities.*;
import com.imbus.knowledge.Content_Management.exception.PostNotFoundException;
import com.imbus.knowledge.Content_Management.repositories.*;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.entities.UserRole;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;
    private final PostReactionRepository reactionRepository;
    private final ReportedPostRepository reportedPostRepository;
    private final ShareRepository shareRepository;

    // ===== POST CRUD =====



    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    public Page<Post> getAllPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size));
    }

    public Post updatePost(Long postId, CreatePostRequest request, Long userId) {
        Post post = getPostById(postId);

        if (!post.getAuthor().getId().equals(userId) && !isUserAdmin(userId)) {
            throw new SecurityException("You are not authorized to update this post.");
        }

        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public void deletePost(Long postId, Long userId) {
        Post post = getPostById(postId);

        if (!isUserAdmin(userId)) {
            throw new SecurityException("Only admins can delete posts.");
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

        boolean alreadyFavorited = favoriteRepository.existsByUserAndPost(user, post);

        if (alreadyFavorited) {
            Favorite favorite = favoriteRepository.findByUserAndPost(user, post);
            favoriteRepository.delete(favorite);
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

        PostReaction existingPostReaction = reactionRepository.findByUserAndPost(user, post);

        if (existingPostReaction != null) {
            existingPostReaction.setEmoji(request.getType());
            reactionRepository.save(existingPostReaction);
        } else {
            PostReaction newPostReaction = new PostReaction();
            newPostReaction.setUser(user);
            newPostReaction.setPost(post);
            newPostReaction.setEmoji(request.getType());
            newPostReaction.setCreatedAt(LocalDateTime.now());
            reactionRepository.save(newPostReaction);
        }


    }

    public void addCommentToPost(Long postId, CommentRequest request, Long userId) {
        Post post = getPostById(postId);
        User user = getUserById(userId);

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);
    }

    public void replyToComment(Long commentId, CommentRequest request, Long userId) {
        Comment parent = getCommentById(commentId);
        User user = getUserById(userId);

        Comment reply = new Comment();
        reply.setText(request.getText());
        reply.setPost(parent.getPost()); // Same post
        reply.setAuthor(user);
        reply.setParent(parent);
        reply.setCreatedAt(LocalDateTime.now());

        commentRepository.save(reply);
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
        Favorite favorite = favoriteRepository.findByUserAndPost(user, post);
        if (favorite != null) {
            favoriteRepository.delete(favorite);
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
    }
    public Post createPost(CreatePostRequest request, Long userId) {
        User author = getUserById(userId);

        if (author.getRole() != UserRole.ADMIN && author.getRole() != UserRole.CONTRIBUTOR) {
            throw new SecurityException("Only Contributor or Admin can create posts.");
        }

        Post post = new Post();
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());

        // ✅ Set category and tags
        post.setCategory(request.getCategory());
        post.setTags(request.getTags());

        return postRepository.save(post);
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
}