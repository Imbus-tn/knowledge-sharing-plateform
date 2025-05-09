package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Comment;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.entities.Reaction;
import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Reaction findByUserAndPost(User user, Post post);
    Reaction findByUserAndComment(User user, Comment comment);
}