package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Favorite;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Use IDs instead of entities
    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);

    Optional<Favorite> findByUser_IdAndPost_Id(Long userId, Long postId);

    @EntityGraph(attributePaths = "post")
    Page<Favorite> findByUser_Id(Long userId, Pageable pageable);

    long countByUser_Id(Long userId);
}