package com.analytics.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DashboardStatsDto {
    private MetricData totalUsers;
    // private MetricData blogPosts;
    private MetricData moodEntries;
}
