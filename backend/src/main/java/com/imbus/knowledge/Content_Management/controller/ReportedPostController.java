package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.ReportRequest;
import com.imbus.knowledge.Content_Management.dto.WarnRequest;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.entities.ReportedPost;
import com.imbus.knowledge.Content_Management.services.PostService;
import com.imbus.knowledge.Content_Management.services.ReportedPostService;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import com.imbus.knowledge.User_Management.dto.MailBody;
import com.imbus.knowledge.User_Management.services.EmailService;
import com.imbus.knowledge.User_Management.entities.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/content/reports")
@RequiredArgsConstructor
public class ReportedPostController {

    private final ReportedPostService reportedPostService;
    private final PostService postService;
    private final EmailService emailService;


    @PostMapping("/{postId}/report")
    public ResponseEntity<ReportedPost> reportPost(
            @PathVariable Long postId,
            @RequestBody ReportRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Post post = postService.getPostById(postId);
        User reporter = userDetails.getUser();

        ReportedPost report = reportedPostService.reportPost(post, reporter, request.getReason());

        return ResponseEntity.ok(report);
    }

    /**
     * GET /api/content/posts/reports
     * Admin-only: Get all reported posts
     */
    @GetMapping("/reports")
    public ResponseEntity<List<ReportedPost>> getAllReportedPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (!isAdmin(userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<ReportedPost> reports = reportedPostService.getAllReportedPosts();
        return ResponseEntity.ok(reports);
    }

    /**
     * POST /api/content/posts/reports/warn/{postId}
     * Admin-only: Send a warning email to the post author
     */
    @PostMapping("/reports/warn/{postId}")
    public ResponseEntity<Void> sendWarningToAuthor(
            @PathVariable Long postId,
            @RequestBody WarnRequest warnRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (!isAdmin(userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Post post = postService.getPostById(postId);
        User author = post.getAuthor();

        // Build the warning email
        MailBody mail = MailBody.builder()
                .to(author.getEmail())
                .subject("Content Warning")
                .text(warnRequest.getMessage())
                .build();

        try {
            emailService.sendSimpleMessage(mail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    // Helper: Check if user is admin
    private boolean isAdmin(UserDetailsImpl userDetails) {
        return UserRole.ADMIN.equals(userDetails.getUser().getRole());
    }
}