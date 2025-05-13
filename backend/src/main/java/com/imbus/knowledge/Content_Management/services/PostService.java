package com.imbus.knowledge.Content_Management.services;

import com.imbus.knowledge.Content_Management.dto.*;
import com.imbus.knowledge.Content_Management.entities.*;
import com.imbus.knowledge.Content_Management.exception.PostNotFoundException;
import com.imbus.knowledge.Content_Management.repositories.*;
import com.imbus.knowledge.User_Management.entities.User;
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
    private final ReactionRepository reactionRepository;
    private final ReportedPostRepository reportedPostRepository;
    private final ShareRepository shareRepository;

    // ===== POST CRUD =====

    public PostResponse createPost(CreatePostRequest request, Long userId) {
        User author = getUserById(userId);

        Post post = new Post();
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost); // Map to DTO
    }

    private Post getPostEntityById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    public PostResponse getPostById(Long postId) {
        Post post = getPostEntityById(postId);
        return PostResponse.from(post);
    }

    public Page<PostResponse> getAllPosts(int page, int size) {
        return postRepository.findAllWithDetails(PageRequest.of(page, size))
                .map(PostResponse::from);
    }

    public PostResponse updatePost(Long postId, CreatePostRequest request, Long userId) {
        Post post = getPostEntityById(postId);
        if (!post.getAuthor().getId().equals(userId)) {
            throw new SecurityException("You are not authorized to update this post.");
        }

        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setUpdatedAt(LocalDateTime.now());

        return PostResponse.from(postRepository.save(post)); // Map to DTO
    }

    public void deletePost(Long postId, Long userId) {
        Post post = getPostEntityById(postId);
        if (!post.getAuthor().getId().equals(userId)) {
            throw new SecurityException("You are not authorized to delete this post.");
        }

        postRepository.delete(post);
    }

    // ===== INTERACTIONS =====

    public void toggleFavorite(Long postId, Long userId) {
        Post post = getPostEntityById(postId);
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
        Post post = getPostEntityById(postId);
        User user = getUserById(userId);

        Reaction existingReaction = reactionRepository.findByUserAndPost(user, post);

        if (existingReaction != null) {
            existingReaction.setType(request.getType());
            reactionRepository.save(existingReaction);
        } else {
            Reaction reaction = new Reaction();
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setType(request.getType());
            reaction.setCreatedAt(LocalDateTime.now());
            reactionRepository.save(reaction);
        }
    }

    public void addCommentToPost(Long postId, CommentRequest request, Long userId) {
        Post post = getPostEntityById(postId);
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

    @Transactional
    public void reactToComment(Long commentId, ReactionRequest request, Long userId) {
        Comment comment = getCommentById(commentId);
        User user = getUserById(userId);

        Reaction existingReaction = reactionRepository.findByUserAndComment(user, comment);

        if (existingReaction != null) {
            existingReaction.setType(request.getType());
            reactionRepository.save(existingReaction);
        } else {
            Reaction reaction = new Reaction();
            reaction.setUser(user);
            reaction.setComment(comment);
            reaction.setType(request.getType());
            reaction.setCreatedAt(LocalDateTime.now());
            reactionRepository.save(reaction);
        }
    }

    public void reportPost(Long postId, ReportRequest request, User reporter) {
        Post post = getPostEntityById(postId);

        ReportedPost reportedPost = new ReportedPost();
        reportedPost.setPost(post);
        reportedPost.setReporter(reporter);
        reportedPost.setReason(request.getReason());
        reportedPost.setReportedAt(LocalDateTime.now());

        reportedPostRepository.save(reportedPost);
    }

    public void sharePost(Long postId, Long userId) {
        Post post = getPostEntityById(postId);
        User user = getUserById(userId);

        Share share = new Share();
        share.setPost(post);
        share.setUser(user);
        share.setSharedAt(LocalDateTime.now());

        shareRepository.save(share);
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