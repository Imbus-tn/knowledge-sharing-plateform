package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.beans.factory.annotation.Value;


import java.time.LocalDateTime;
import java.util.List;

public interface PostSummary {
    Long getId();
    String getTitle();
    String getContent();
    String getImageUrl();
    String getVideoUrl();
    String getVideoThumbnail();
    LocalDateTime getCreatedAt();
    User getAuthor();

    @Value("#{target.comments.size()}")
    Integer getCommentCount();

    @Value("#{target.tags.size()}")
    Integer getTagCount();

    @Value("#{target.reactions.size()}")
    Integer getReactionCount();

    @Value("#{target.tags}")
    List<TagDto> getTagDtos();

    @Value("#{target.reactions}")
    List<ReactionDto> getReactionDtos();

    @Value("#{target.shares.size()}")
    Integer getShareCount();
}
