package com.imbus.knowledge.Content_Management.services;

import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.entities.ReportedPost;
import com.imbus.knowledge.User_Management.dto.MailBody;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.Content_Management.repositories.ReportedPostRepository;
import com.imbus.knowledge.Content_Management.repositories.PostRepository;
import com.imbus.knowledge.User_Management.entities.WarningMessage;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.User_Management.repositories.WarningMessageRepository;
import com.imbus.knowledge.User_Management.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportedPostService {

    private final ReportedPostRepository reportedPostRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final EmailService emailService; // Optional: send email warning

    @Transactional
    public ReportedPost reportPost(Post post, User reporter, String reason) {
        if (reportedPostRepository.findByPostId(post.getId()).isPresent()) {
            throw new RuntimeException("Post already reported");
        }

        ReportedPost report = new ReportedPost();
        report.setPost(post);
        report.setReporter(reporter);
        report.setReason(reason);
        report.setReportedAt(LocalDateTime.now());

        return reportedPostRepository.save(report);
    }

    public List<ReportedPost> getAllReportedPosts() {
        return reportedPostRepository.findAll();
    }

    @Transactional
    public void sendWarningToAuthor(Long postId, String message) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User author = post.getAuthor();
        // In real app: send email or in-app notification
        System.out.println("⚠️ Warning to " + author.getEmail() + ": " + message);
        // You could save this in a `UserWarning` table
    }

    @Transactional
    public void deletePostAndMarkReported(Long reportId) {
        ReportedPost report = reportedPostRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        Post post = report.getPost();
        postRepository.delete(post); // Cascades will remove reactions, comments, etc.

        // Optional: keep report for audit
        report.setPost(null);
        reportedPostRepository.save(report);
    }
    @Autowired
    private WarningMessageRepository warningRepo;

    @Autowired
    private UserRepository userRepo;

    @Transactional
    public void sendWarningToAuthor(Long postId, String message, Long adminId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User author = post.getAuthor();
        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Save warning in DB
        WarningMessage warning = new WarningMessage();
        warning.setUser(author);
        warning.setAdmin(admin);
        warning.setMessage(message);
        warning.setCreatedAt(LocalDateTime.now());
        warningRepo.save(warning);

        // Also send email
        MailBody mail = MailBody.builder()
                .to(author.getEmail())
                .subject("Account Warning")
                .text(message)
                .build();
        emailService.sendSimpleMessage(mail);
    }
}