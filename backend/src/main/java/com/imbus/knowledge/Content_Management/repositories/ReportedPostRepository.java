package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.entities.ReportedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportedPostRepository extends JpaRepository<ReportedPost, Long> {
    @EntityGraph(attributePaths = {"post", "reporter"})
    @Query("SELECT r FROM ReportedPost r")
    Page<ReportedPost> findAllReportedPosts(Pageable pageable);

    @EntityGraph(attributePaths = "post")
    List<ReportedPost> findByPost(Post post);

    long countByPost(Post post);
}