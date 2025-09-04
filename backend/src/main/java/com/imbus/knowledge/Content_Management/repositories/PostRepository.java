package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH c.author " +
            "LEFT JOIN FETCH p.postReactions r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH p.favorites f " +
            "LEFT JOIN FETCH f.user " +
            "LEFT JOIN FETCH p.shares s " +
            "LEFT JOIN FETCH s.user " +
            "WHERE p.id = :id")
    Optional<Post> findWithDetailsById(@Param("id") Long id);
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author ORDER BY p.createdAt DESC")
    Page<Post> findAllWithAuthors(Pageable pageable);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Post> findByCategory(String category);

    // ✅ Correct: Traverse relationship
    List<Post> findByAuthor_Id(Long authorId);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);
    @EntityGraph(attributePaths = {
            "author"
    })
    // PostRepository.java
    @Query("SELECT p FROM Post p WHERE p.createdAt > :since ORDER BY p.createdAt DESC")
    Page<Post> findLatestPosts(@Param("since") Instant since, Pageable pageable);

    @EntityGraph(attributePaths = {
            "author"
    })
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findLatestPostsOrdered(Pageable pageable);
}