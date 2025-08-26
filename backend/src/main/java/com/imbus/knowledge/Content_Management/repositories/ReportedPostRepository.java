package com.imbus.knowledge.Content_Management.repositories;

import com.imbus.knowledge.Content_Management.entities.ReportedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportedPostRepository extends JpaRepository<ReportedPost, Long> {
    Optional<ReportedPost> findByPostId(Long postId);
    Optional<ReportedPost> findByPostIdAndReporterId(Long postId, Long reporterId);
}
