package com.analytics.services.Impl;

import com.analytics.DTOs.DashboardStatsDto;
import com.analytics.DTOs.MetricData;
import com.analytics.client.AuthClient;
import com.analytics.client.BlogClient;
import com.analytics.client.MoodClient;
import com.analytics.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {
    private final AuthClient authClient;
    private final BlogClient blogClient;
    private final MoodClient moodClient;

    // @Cacheable(value = "dashboardStats", key = "'main'")
    // @Override
    // public Mono<DashboardStatsDto> getDashboardStats() {
    //     log.info("Obteniendo estadísticas del dashboard");

    //     Mono<MetricData> moodStats = getMoodStats();

    //     return moodStats.map(moodMetric -> DashboardStatsDto.builder()
    //             .moodEntries(moodMetric)
    //             .build());
    // }
    @Cacheable(value = "dashboardStats", key = "'main'")
    @Override
    public Mono<DashboardStatsDto> getDashboardStats() {
    log.info("Obteniendo estadísticas del dashboard");

    Mono<MetricData> userStats = getUserStats();
    // Mono<MetricData> blogStats = getBlogStats();
    Mono<MetricData> moodStats = getMoodStats();

    // return Mono.zip(userStats, blogStats, moodStats)
    return Mono.zip(userStats, moodStats)
    .map(tuple -> DashboardStatsDto.builder()
    .totalUsers(tuple.getT1())
    // .blogPosts(tuple.getT2())
    .moodEntries(tuple.getT2())
    .build());
    }

    private Mono<MetricData> getUserStats() {
        return Mono.zip(
                authClient.getTotalUsers(),
                authClient.getPreviousMonthUsers()).map(tuple -> {
                    Long currentCount = tuple.getT1();
                    Long previousCount = tuple.getT2();
                    double percentChange = calculatePercentageChange(previousCount, currentCount);

                    return MetricData.builder()
                            .currentValue(currentCount)
                            .percentageChange(percentChange)
                            .build();
                }).onErrorReturn(MetricData.builder().currentValue(0L).percentageChange(0.0).build());
    }

    private Mono<MetricData> getBlogStats() {
        return Mono.zip(
                blogClient.getTotalBlogs(),
                blogClient.getPreviousMonthBlogs()).map(tuple -> {
                    Long currentCount = tuple.getT1();
                    Long previousCount = tuple.getT2();
                    double percentChange = calculatePercentageChange(previousCount, currentCount);

                    return MetricData.builder()
                            .currentValue(currentCount)
                            .percentageChange(percentChange)
                            .build();
                }).onErrorReturn(MetricData.builder().currentValue(0L).percentageChange(0.0).build());
    }

    private Mono<MetricData> getMoodStats() {
        return Mono.zip(
                moodClient.getTotalMoods(),
                moodClient.getPreviousMonthMoods()).map(tuple -> {
                    Long currentCount = tuple.getT1();
                    Long previousCount = tuple.getT2();
                    double percentChange = calculatePercentageChange(previousCount, currentCount);

                    return MetricData.builder()
                            .currentValue(currentCount)
                            .percentageChange(percentChange)
                            .build();
                }).onErrorReturn(MetricData.builder().currentValue(0L).percentageChange(0.0).build());
    }

    private double calculatePercentageChange(Long previous, Long current) {
        if (previous == null || previous == 0) {
            return 0.0;
        }
        return Math.round(((double) (current - previous) / previous * 100) * 10.0) / 10.0;
    }
}
