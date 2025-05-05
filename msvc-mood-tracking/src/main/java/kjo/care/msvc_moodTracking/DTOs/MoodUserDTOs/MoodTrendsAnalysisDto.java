package kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoodTrendsAnalysisDto {
    private String timePeriod;
    private Long totalEntries;

    private String mostCommonMood;
    private Double mostCommonMoodPercentage;


    private String variabilityLevel;
    private Double variabilityScore;

    private String trendDirection;
    private Double weeklyTrendScore;

    // private Map<String, Long> moodDistribution;
    // private Map<String, Double> weeklyAverages;
    // private Map<String, Double> monthlyAverages;

}
