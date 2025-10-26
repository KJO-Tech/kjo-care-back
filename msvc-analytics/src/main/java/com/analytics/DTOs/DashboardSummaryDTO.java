package com.analytics.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryDTO {
    private DailyActivitySummaryDTO dailyActivitySummary;
    private long moodLogDays;
    private long averageBlogLikes;
    private Double averageMood;
}
