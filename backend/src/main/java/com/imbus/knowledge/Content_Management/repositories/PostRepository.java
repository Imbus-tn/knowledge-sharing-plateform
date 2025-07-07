package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Add this method ↓↓↓
    List<Post> findByAuthorId(Long authorId);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}