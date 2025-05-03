package kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoodStatisticsDto {
    private Map<String, Long> moodCounts;
    private Map<String, Double> moodPercentages;
    private Long totalMoods;
    private String timePeriod;
}
