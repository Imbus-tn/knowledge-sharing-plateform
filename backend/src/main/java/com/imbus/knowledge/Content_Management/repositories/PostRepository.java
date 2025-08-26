package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.postReactions WHERE p.id = :id")
    Optional<Post> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author ORDER BY p.createdAt DESC")
    Page<Post> findAllWithAuthors(Pageable pageable);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Post> findByCategory(String category);

    // ✅ Correct: Traverse relationship
    List<Post> findByAuthor_Id(Long authorId);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);
}