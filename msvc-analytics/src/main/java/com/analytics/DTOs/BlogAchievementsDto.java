package com.analytics.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogAchievementsDto {
    private Long countBlogs;
    private Long countReactions;
    private Long countComments;
}
