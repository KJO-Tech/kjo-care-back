package com.analytics.services;

import com.analytics.DTOs.DashboardStatsDto;
import reactor.core.publisher.Mono;

public interface AnalyticsService {
    Mono<DashboardStatsDto> getDashboardStats();
}
