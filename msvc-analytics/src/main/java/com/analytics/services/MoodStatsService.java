package com.analytics.services;

import com.analytics.DTOs.*;
import com.analytics.client.MoodTrackingFeignClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MoodStatsService {

    private final MoodTrackingFeignClient moodClient;

    public MoodStatsService(MoodTrackingFeignClient moodClient) {
        this.moodClient = moodClient;
    }

    public ApiResponseDto<List<HeatmapEntryDto>> getUserMoodHeatmap(String userId) {
        try {
            ApiResponseDto<List<MoodRegisterDto>> response = moodClient.getMoodRegistersByUser(userId);

            if (!response.isSuccess() || response.getResult() == null) {
                return new ApiResponseDto<>() {{
                    setStatusCode(404);
                    setMessage("No se encontraron registros de estado de ánimo");
                    setSuccess(false);
                    setResult(Collections.emptyList());
                }};
            }


            Map<java.time.LocalDate, Integer> grouped = response.getResult().stream()
                    .collect(Collectors.groupingBy(
                            MoodRegisterDto::getDate,
                            Collectors.averagingInt(MoodRegisterDto::getValue)
                    ))
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().intValue()
                    ));

            List<HeatmapEntryDto> heatmap = grouped.entrySet().stream()
                    .map(e -> new HeatmapEntryDto(e.getKey(), e.getValue()))
                    .sorted(Comparator.comparing(HeatmapEntryDto::getDate))
                    .collect(Collectors.toList());

            ApiResponseDto<List<HeatmapEntryDto>> result = new ApiResponseDto<>();
            result.setStatusCode(200);
            result.setMessage("Heatmap generado correctamente");
            result.setSuccess(true);
            result.setResult(heatmap);

            return result;

        } catch (Exception e) {
            ApiResponseDto<List<HeatmapEntryDto>> error = new ApiResponseDto<>();
            error.setStatusCode(500);
            error.setMessage("Error al generar el heatmap: " + e.getMessage());
            error.setSuccess(false);
            error.setResult(Collections.emptyList());
            return error;
        }
    }

    public ApiResponseDto<List<MoodPieChartEntryDto>> getUserMoodPieChart(String userId, String range) {
        try {
            ApiResponseDto<List<MoodRegisterDto>> response = moodClient.getMoodRegistersByUser(userId);

            if (!response.isSuccess() || response.getResult() == null) {
                return new ApiResponseDto<>() {{
                    setStatusCode(404);
                    setMessage("No se encontraron registros de estado de ánimo");
                    setSuccess(false);
                    setResult(Collections.emptyList());
                }};
            }


            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate minDate = switch (range) {
                case "7d" -> now.minusDays(7);
                case "1m" -> now.minusMonths(1);
                case "1y" -> now.minusYears(1);
                default -> now.minusDays(7);
            };

            List<MoodRegisterDto> filtered = response.getResult().stream()
                    .filter(m -> m.getDate() != null && !m.getDate().isBefore(minDate))
                    .toList();

            if (filtered.isEmpty()) {
                return new ApiResponseDto<>() {{
                    setStatusCode(204);
                    setMessage("No hay registros en el rango especificado");
                    setSuccess(true);
                    setResult(Collections.emptyList());
                }};
            }


            Map<Integer, Long> countByMood = filtered.stream()
                    .collect(Collectors.groupingBy(MoodRegisterDto::getValue, Collectors.counting()));

            long total = filtered.size();

            List<MoodPieChartEntryDto> chartData = countByMood.entrySet().stream()
                    .map(e -> new MoodPieChartEntryDto(
                            e.getKey(),
                            e.getValue(),
                            (e.getValue() * 100.0) / total
                    ))
                    .sorted(Comparator.comparing(MoodPieChartEntryDto::getMoodValue))
                    .collect(Collectors.toList());

            ApiResponseDto<List<MoodPieChartEntryDto>> result = new ApiResponseDto<>();
            result.setStatusCode(200);
            result.setMessage("Gráfico circular generado correctamente");
            result.setSuccess(true);
            result.setResult(chartData);

            return result;

        } catch (Exception e) {
            ApiResponseDto<List<MoodPieChartEntryDto>> error = new ApiResponseDto<>();
            error.setStatusCode(500);
            error.setMessage("Error al generar el gráfico circular: " + e.getMessage());
            error.setSuccess(false);
            error.setResult(Collections.emptyList());
            return error;
        }
    }

    public ApiResponseDto<List<MoodRadarChartEntryDto>> getUserMoodRadarChart(String userId, String range) {
        try {
            ApiResponseDto<List<MoodRegisterDto>> response = moodClient.getMoodRegistersByUser(userId);

            if (!response.isSuccess() || response.getResult() == null) {
                return new ApiResponseDto<>() {{
                    setStatusCode(404);
                    setMessage("No se encontraron registros de estado de ánimo");
                    setSuccess(false);
                    setResult(Collections.emptyList());
                }};
            }

            // Filtramos por rango de tiempo
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate minDate = switch (range) {
                case "7d" -> now.minusDays(7);
                case "1m" -> now.minusMonths(1);
                case "1y" -> now.minusYears(1);
                default -> now.minusDays(7);
            };

            List<MoodRegisterDto> filtered = response.getResult().stream()
                    .filter(m -> m.getDate() != null && !m.getDate().isBefore(minDate))
                    .toList();

            if (filtered.isEmpty()) {
                return new ApiResponseDto<>() {{
                    setStatusCode(204);
                    setMessage("No hay registros en el rango especificado");
                    setSuccess(true);
                    setResult(Collections.emptyList());
                }};
            }

            // Agrupamos por tipo de estado de ánimo
            Map<Integer, Long> countByMood = filtered.stream()
                    .collect(Collectors.groupingBy(MoodRegisterDto::getValue, Collectors.counting()));

            List<MoodRadarChartEntryDto> chartData = countByMood.entrySet().stream()
                    .map(e -> new MoodRadarChartEntryDto(e.getKey(), e.getValue()))
                    .sorted(Comparator.comparing(MoodRadarChartEntryDto::getMoodValue))
                    .collect(Collectors.toList());

            ApiResponseDto<List<MoodRadarChartEntryDto>> result = new ApiResponseDto<>();
            result.setStatusCode(200);
            result.setMessage("Gráfico radar generado correctamente");
            result.setSuccess(true);
            result.setResult(chartData);

            return result;

        } catch (Exception e) {
            ApiResponseDto<List<MoodRadarChartEntryDto>> error = new ApiResponseDto<>();
            error.setStatusCode(500);
            error.setMessage("Error al generar el gráfico radar: " + e.getMessage());
            error.setSuccess(false);
            error.setResult(Collections.emptyList());
            return error;
        }
    }

    public ApiResponseDto<List<MoodHeatmapEntryDto>> getUserMoodHeatmap(String userId, String range) {
        try {
            ApiResponseDto<List<MoodRegisterDto>> response = moodClient.getMoodRegistersByUser(userId);

            if (!response.isSuccess() || response.getResult() == null) {
                return new ApiResponseDto<>() {{
                    setStatusCode(404);
                    setMessage("No se encontraron registros de estado de ánimo");
                    setSuccess(false);
                    setResult(Collections.emptyList());
                }};
            }


            LocalDate now = LocalDate.now();
            LocalDate minDate = switch (range) {
                case "7d" -> now.minusDays(7);
                case "1m" -> now.minusMonths(1);
                case "1y" -> now.minusYears(1);
                default -> now.minusMonths(1);
            };


            List<MoodRegisterDto> filtered = response.getResult().stream()
                    .filter(m -> m.getDate() != null && !m.getDate().isBefore(minDate))
                    .toList();

            if (filtered.isEmpty()) {
                return new ApiResponseDto<>() {{
                    setStatusCode(204);
                    setMessage("No hay registros en el rango especificado");
                    setSuccess(true);
                    setResult(Collections.emptyList());
                }};
            }


            Map<LocalDate, Double> avgByDate = filtered.stream()
                    .collect(Collectors.groupingBy(
                            MoodRegisterDto::getDate,
                            Collectors.averagingInt(MoodRegisterDto::getValue)
                    ));


            List<MoodHeatmapEntryDto> chartData = avgByDate.entrySet().stream()
                    .map(e -> new MoodHeatmapEntryDto(e.getKey(), (int) Math.round(e.getValue())))
                    .sorted(Comparator.comparing(MoodHeatmapEntryDto::getDate))
                    .toList();

            ApiResponseDto<List<MoodHeatmapEntryDto>> result = new ApiResponseDto<>();
            result.setStatusCode(200);
            result.setMessage("Heatmap generado correctamente");
            result.setSuccess(true);
            result.setResult(chartData);

            return result;

        } catch (Exception e) {
            ApiResponseDto<List<MoodHeatmapEntryDto>> error = new ApiResponseDto<>();
            error.setStatusCode(500);
            error.setMessage("Error al generar el heatmap: " + e.getMessage());
            error.setSuccess(false);
            error.setResult(Collections.emptyList());
            return error;
        }
    }

}
