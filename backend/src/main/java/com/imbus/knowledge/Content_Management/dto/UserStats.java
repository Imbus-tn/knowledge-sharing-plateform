package com.imbus.knowledge.Content_Management.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats {
    private int articlesPublished;
    private String totalViews;
    private int contributions;
    private int reviewsCount;
    private int sharesCount;
    private List<String> recentActivity;
}
