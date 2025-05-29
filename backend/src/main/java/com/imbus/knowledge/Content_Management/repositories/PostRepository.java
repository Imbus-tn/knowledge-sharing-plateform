package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.dto.PostSummary;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // For single post by ID with eager loading
    @EntityGraph(attributePaths = {"author", "comments", "reactions", "tags"})
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findByIdWithDetails(@Param("id") Long id);

    // For listing all posts without MultipleBagFetchException
    @Query("SELECT p.id AS id, p.title AS title, p.content AS content, p.imageUrl AS imageUrl, " +
            "p.videoUrl AS videoUrl, p.videoThumbnail AS videoThumbnail, p.author AS author, " +
            "p.createdAt AS createdAt, COUNT(c.id) AS commentCount, COUNT(t.name) AS tagCount, " +
            "COUNT(r.id) AS reactionCount " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN p.tags t " +
            "LEFT JOIN p.reactions r " +
            "GROUP BY p.id, p.title, p.content, p.imageUrl, p.videoUrl, p.videoThumbnail, p.author, p.createdAt")
    Page<PostSummary> findAllSummaries(Pageable pageable);
}