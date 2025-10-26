package com.analytics.services;

import com.analytics.DTOs.DailyBlogCountDto;
import com.analytics.DTOs.DailyMoodUserCountDto;
import com.analytics.DTOs.DashboardStatsDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AnalyticsService {
    Mono<DashboardStatsDto> getDashboardStats();

    Mono<Long> getTotalHealthCenters();

    Mono<Long> getActiveHealthCenters();

    Mono<Long> getPreviousMonthHealthCenters();

    Mono<List<DailyBlogCountDto>> getDailyBlogsCurrentMonth();

    Mono<List<DailyMoodUserCountDto>> getDailyMoodUsersLastMonth();
    Mono<AnalyticsSummaryDto> getAnalyticsSummary(String userId);
    Mono<DashboardSummaryDTO> getDashboardSummary(String userId);

    Mono<ResourceStatsDto> getResourceStats();

}