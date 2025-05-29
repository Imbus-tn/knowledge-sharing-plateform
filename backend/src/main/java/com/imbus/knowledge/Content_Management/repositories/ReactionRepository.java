package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Comment;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.entities.Reaction;
import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndPost(User user, Post post);
    Optional<Reaction> findByUserAndComment(User user, Comment comment);

    @EntityGraph(attributePaths = {"user", "post", "comment"})
    List<Reaction> findByPost(Post post);

    @EntityGraph(attributePaths = "user")
    List<Reaction> findByComment(Comment comment);

    long countByPost(Post post);
    long countByComment(Comment comment);
}