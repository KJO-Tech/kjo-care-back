package com.analytics.controllers;

import com.analytics.DTOs.*;
import com.analytics.services.MoodStatsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics/mood")
public class MoodStatsController {

    private final MoodStatsService moodStatsService;

    public MoodStatsController(MoodStatsService moodStatsService) {
        this.moodStatsService = moodStatsService;
    }


    @GetMapping("/pie/{userId}")
    public ApiResponseDto<List<MoodPieChartEntryDto>> getUserMoodPieChart(
            @PathVariable String userId,
            @RequestParam(defaultValue = "7d") String range
    ) {
        return moodStatsService.getUserMoodPieChart(userId, range);
    }

    @GetMapping("/radar/{userId}")
    public ApiResponseDto<List<MoodRadarChartEntryDto>> getUserMoodRadarChart(
            @PathVariable String userId,
            @RequestParam(defaultValue = "7d") String range
    ) {
        return moodStatsService.getUserMoodRadarChart(userId, range);
    }

    @GetMapping("/heatmap/{userId}")
    public ApiResponseDto<List<MoodHeatmapEntryDto>> getUserMoodHeatmap(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1m") String range
    ) {
        return moodStatsService.getUserMoodHeatmap(userId, range);
    }

}
