package com.analytics.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryDto {
    private BlogAchievementsDto blogAchievements;
    private long moodLogDays;
}
