package com.analytics.services.Impl;

import com.analytics.DTOs.*;
import com.analytics.client.*;
import com.analytics.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {
    private final AuthClient authClient;
    private final BlogClient blogClient;
    private final MoodClient moodClient;
    private final EmergencyClient emergencyClient;
    private final DailyActivityClient dailyActivityClient;


    @Cacheable(value = "dashboardStats", key = "'main'")
    @Override
    public Mono<DashboardStatsDto> getDashboardStats() {
        log.info("Obteniendo estadísticas del dashboard");

        Mono<MetricData> userStats = getUserStats();
        Mono<MetricData> blogStats = getBlogStats();
        Mono<MetricData> moodStats = getMoodStats();
        Mono<MetricData> healthCenterStats = getHealthCenterStats();

        return Mono.zip(userStats, blogStats, moodStats, healthCenterStats)
                .map(tuple -> DashboardStatsDto.builder()
                        .totalUsers(tuple.getT1())
                        .blogPosts(tuple.getT2())
                        .moodEntries(tuple.getT3())
                        .healthCenters(tuple.getT4())
                        .build());
    }

    private Mono<MetricData> getHealthCenterStats() {
        return Mono.zip(
                emergencyClient.getTotalHealthCenters(),
                emergencyClient.getPreviousMonthHealthCenters()).map(tuple -> {
                    Long currentCount = tuple.getT1();
                    Long previousCount = tuple.getT2();
                    double percentChange = calculatePercentageChange(previousCount, currentCount);

                    return MetricData.builder()
                            .currentValue(currentCount)
                            .percentageChange(percentChange)
                            .build();
                }).onErrorReturn(MetricData.builder().currentValue(0L).percentageChange(0.0).build());
    }

    @Override
    public Mono<Long> getTotalHealthCenters() {
        log.info("Obteniendo total de centros de salud");
        return emergencyClient.getTotalHealthCenters();
    }

    @Override
    public Mono<Long> getActiveHealthCenters() {
        log.info("Obteniendo centros de salud activos");
        return emergencyClient.getActiveHealthCenters();
    }

    @Override
    public Mono<Long> getPreviousMonthHealthCenters() {
        log.info("Obteniendo centros de salud del mes anterior");
        return emergencyClient.getPreviousMonthHealthCenters();
    }

    private Mono<MetricData> getUserStats() {
        return Mono.zip(
                authClient.getAllUsers(),
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
            return current > 0 ? 100.0 : 0.0;
        }

        double change = ((current - previous) * 100.0) / previous;
        return Math.round(change * 10) / 10.0;
    }

    @Override
    @Cacheable(value = "dailyBlogsStats", key = "'current-month'")
    public Mono<List<DailyBlogCountDto>> getDailyBlogsCurrentMonth() {
        log.info("Obteniendo estadísticas diarias de blogs del mes actual");

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        log.debug("Consultando blogs entre {} y {}", startOfMonth, endOfMonth);

        return blogClient.getBlogCountsByDayBetweenDates(startOfMonth, endOfMonth)
                .onErrorResume(ex -> {
                    log.error("Error al obtener datos del servicio de blogs. Generando datos vacíos.", ex);
                    return Mono.just(List.of());
                })
                .map(results -> {
                    List<DailyBlogCountDto> dailyCounts = new ArrayList<>();

                    try {
                        for (Object[] result : results) {
                            if (result.length >= 2) {
                                LocalDate localDate;
                                if (result[0] instanceof java.sql.Date) {
                                    localDate = ((java.sql.Date) result[0]).toLocalDate();
                                } else {

                                    log.warn("Formato de fecha desconocido: {}. Saltando registro.", result[0]);
                                    continue;
                                }

                                Long count;
                                if (result[1] instanceof Number) {
                                    count = ((Number) result[1]).longValue();
                                } else {
                                    log.warn("Formato de conteo desconocido: {}. Usando 0.", result[1]);
                                    count = 0L;
                                }

                                log.debug("Fecha procesada: {}, Conteo: {}", localDate, count);
                                dailyCounts.add(new DailyBlogCountDto(localDate, count));
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error al procesar resultados de blogs diarios: {}", e.getMessage());
                    }


                    List<DailyBlogCountDto> completeDailyCounts = new ArrayList<>();
                    Map<LocalDate, Long> countsMap = dailyCounts.stream()
                            .collect(Collectors.toMap(DailyBlogCountDto::getDate, DailyBlogCountDto::getCount,
                                    (v1, v2) -> v1));

                    LocalDate date = startOfMonth;
                    while (!date.isAfter(endOfMonth)) {
                        Long count = countsMap.getOrDefault(date, 0L);
                        completeDailyCounts.add(new DailyBlogCountDto(date, count));
                        date = date.plusDays(1);
                    }

                    return completeDailyCounts;
                });
    }

    @Override
    @Cacheable(value = "dailyMoodUsersStats", key = "'last-month'")
    public Mono<List<DailyMoodUserCountDto>> getDailyMoodUsersLastMonth() {
        log.info("Obteniendo estadísticas diarias de usuarios con estados de ánimo registrados en el último mes");

        return moodClient.getMoodUserCountsByDayLastMonth()
                .onErrorResume(ex -> {
                    log.error("Error al obtener datos del servicio de estados de ánimo. Generando datos vacíos.", ex);
                    return Mono.just(List.of());
                })
                .map(results -> {
                    List<DailyMoodUserCountDto> dailyCounts = new ArrayList<>();


                    try {
                        for (Object[] result : results) {
                            if (result.length >= 2) {
                                LocalDate localDate;
                                if (result[0] instanceof Date) {
                                    localDate = ((Date) result[0]).toLocalDate();
                                } else {
                                    log.warn("Formato de fecha desconocido: {}. Saltando registro.", result[0]);
                                    continue;
                                }

                                Long count;
                                if (result[1] instanceof Number) {
                                    count = ((Number) result[1]).longValue();
                                } else {
                                    log.warn("Formato de conteo desconocido: {}. Usando 0.", result[1]);
                                    count = 0L;
                                }

                                log.debug("Fecha procesada: {}, Conteo de usuarios: {}", localDate, count);
                                dailyCounts.add(new DailyMoodUserCountDto(localDate, count));
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error al procesar resultados de usuarios diarios: {}", e.getMessage());
                    }

                    return dailyCounts;
                });
    }

    public Mono<AnalyticsSummaryDto> getAnalyticsSummary(String userId) {
        log.info("Obteniendo resumen de analíticas para el usuario: {}", userId);

        Mono<BlogAchievementsDto> blogAchievementsMono = blogClient.getBlogAchievements(userId)
                .doOnNext(achievements -> log.info("Logros del blog obtenidos: {}", achievements))
                .onErrorResume(error -> {
                    log.error("Error al obtener logros del blog: {}", error.getMessage());
                    return Mono.just(new BlogAchievementsDto(0L, 0L, 0L)); // Valores por defecto
                });

        Mono<Long> moodLogDaysMono = moodClient.getMoodLogDays(userId)
                .doOnNext(days -> log.info("Días de registro de ánimo obtenidos: {}", days))
                .onErrorResume(error -> {
                    log.error("Error al obtener días de registro de ánimo: {}", error.getMessage());
                    return Mono.just(0L); // Valor por defecto
                });

        return Mono.zip(blogAchievementsMono, moodLogDaysMono)
                .map(tuple -> new AnalyticsSummaryDto(tuple.getT1(), tuple.getT2()))
                .doOnSuccess(summary -> log.info("Resumen de analíticas obtenido exitosamente: {}", summary))
                .doOnError(error -> log.error("Error al obtener el resumen de analíticas: {}", error.getMessage()));
    }

    @Override
    public Mono<DashboardSummaryDTO> getDashboardSummary(String userId) {
        log.info("Obteniendo resumen de analíticas para el usuario: {}", userId);

        Mono<DailyActivitySummaryDTO> activitySummary = dailyActivityClient.getDailyActivitySummary(userId)
                .doOnNext(activity -> log.info("Datos obtenidos correctamente: {}", activity))
                .onErrorResume(error -> {
                    log.error("Error al obtener las actividades: {}", error.getMessage());
                    return Mono.just(new DailyActivitySummaryDTO(0L, 0L));
                });

        Mono<Long> moodLogDaysMono = moodClient.getMoodLogDays(userId)
                .doOnNext(days -> log.info("Días de registro de ánimo obtenidos: {}", days))
                .onErrorResume(error -> {
                    log.error("Error al obtener días de registro de ánimo: {}", error.getMessage());
                    return Mono.just(0L);
                });

        Mono<Long> averageBlogReaction = blogClient.getAverageBlogReaction(userId)
                .doOnNext(average -> log.info("Promedio de reacciones de blogs obtenidos: {}", average))
                .onErrorResume(error -> {
                    log.error("Error al obtener el promedio de reacciones: {}", error.getMessage());
                    return Mono.just(0L);
                });

        return Mono.zip(activitySummary, moodLogDaysMono, averageBlogReaction)
                .map(tuple -> new DashboardSummaryDTO(tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .doOnSuccess(summary -> log.info("Resumen de analíticas obtenido exitosamente: {}", summary))
                .doOnError(error -> log.error("Error al obtener el resumen de analíticas: {}", error.getMessage()));
    }
}
