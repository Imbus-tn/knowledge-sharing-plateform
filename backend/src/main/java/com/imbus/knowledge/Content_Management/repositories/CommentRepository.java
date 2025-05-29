package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Comment;
import com.imbus.knowledge.Content_Management.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"author", "replies"})
    List<Comment> findByPostAndParentIsNullOrderByCreatedAtDesc(Post post);

    @EntityGraph(attributePaths = {"author", "replies"})
    Page<Comment> findByParentOrderByCreatedAtDesc(Comment parent, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "replies", "reactions", "parent"})
    @Query("SELECT c FROM Comment c WHERE c.id = :id")
    Optional<Comment> findByIdWithDetails(@Param("id") Long id);
}