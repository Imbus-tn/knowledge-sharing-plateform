package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Comment;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.entities.PostReaction;
import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    PostReaction findByUserAndPost(User user, Post post);
    PostReaction findByUserAndComment(User user, Comment comment);
    @Query("SELECT r.emoji, COUNT(r) FROM PostReaction r WHERE r.post.id = :postId GROUP BY r.emoji")
    List<Object[]> countByPostIdGroupedByEmoji(@Param("postId") Long postId);
}