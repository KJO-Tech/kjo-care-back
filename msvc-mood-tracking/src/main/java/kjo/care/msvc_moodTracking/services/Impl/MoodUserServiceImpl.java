package kjo.care.msvc_moodTracking.services.Impl;

import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.*;
import kjo.care.msvc_moodTracking.Entities.MoodEntity;
import kjo.care.msvc_moodTracking.Entities.MoodUser;
import kjo.care.msvc_moodTracking.Repositories.MoodRepository;
import kjo.care.msvc_moodTracking.Repositories.MoodUserRepository;
import kjo.care.msvc_moodTracking.exceptions.MoodEntityNotFoundException;
import kjo.care.msvc_moodTracking.services.MoodUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
@Log4j2
public class MoodUserServiceImpl implements MoodUserService {
    private final MoodUserRepository moodUserRepository;
    private final MoodRepository moodRepository;
    private final WebClient.Builder webClient;
    private final ModelMapper modelMapper;

    @Value("${microservice.users.url}")
    private String userServiceUrl;

    @Override
    public Mono<UserDTO> getUserById(String id) {
        log.info("Obteniendo usuario por ID: {}", id);

        return webClient.build()
                .get()
                .uri(userServiceUrl + "/{userId}", id)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .doOnError(error -> {
                    log.error("Error al obtener usuario con ID {} : {}", id, error.getMessage());
                })
                .onErrorResume(error -> {
                    log.warn("Fallback: Creando información básica del usuario debido a error: {}", error.getMessage());
                    UserDTO basicUser = new UserDTO();
                    basicUser.setId(id);
                    basicUser.setUsername("Usuario " + id.substring(0, 8));
                    basicUser.setFirstName("Usuario");
                    basicUser.setLastName("Temporal");
                    return Mono.just(basicUser);
                });
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<UserMoodDTO> getCurrentUserMoods(String userId) {
        log.info("Obteniendo estados de animo del usuario : {}", userId);
        return Mono.fromCallable(() -> moodUserRepository.findByUserIdWithMoodOrderByCreatedAtDesc(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .flatMap(moodUser -> getUserById(userId)
                        .map(userDTO -> UserMoodDTO.builder()
                                .id(moodUser.getId())
                                .user(userDTO)
                                .description(moodUser.getDescription())
                                .mood(modelMapper.map(moodUser.getMood(), MoodResponseDto.class))
                                .recordedDate(moodUser.getRecordedDate())
                                .build())
                        .onErrorResume(e -> {
                            log.error("Error obteniendo informacion del usuario {} : {}", userId, e.getMessage());
                            UserDTO basicUserDTO = new UserDTO();
                            basicUserDTO.setId(userId);
                            basicUserDTO.setUsername("Usuario " + userId.substring(0, 8));

                            return Mono.just(UserMoodDTO.builder()
                                    .id(moodUser.getId())
                                    .user(basicUserDTO)
                                    .mood(modelMapper.map(moodUser.getMood(), MoodResponseDto.class))
                                    .recordedDate(moodUser.getRecordedDate())
                                    .build());
                        }));
    }

    @Transactional
    @Override
    public UserMoodDTO trackUserMood(String userId, MoodUserRequestDto moodUserRequestDto) {
        log.info("Registrando estado de ánimo para usuario: {}", userId);

        MoodEntity mood = moodRepository.findById(moodUserRequestDto.moodId())
                .orElseThrow(() -> new MoodEntityNotFoundException(
                        "Estado de ánimo no encontrado: " + moodUserRequestDto.moodId()));

        if (!mood.getIsActive()) {
            throw new IllegalArgumentException("Este estado de ánimo no está disponible para selección");
        }

        MoodUser moodUser = MoodUser.builder()
                .userId(userId)
                .mood(mood)
                .recordedDate(LocalDateTime.now())
                .description(moodUserRequestDto.description())
                .build();

        MoodUser savedMoodUser = moodUserRepository.save(moodUser);
        log.info("Estado de animo registrado con id:{}", savedMoodUser.getId());

        UserDTO userInfo = new UserDTO();
        userInfo.setId(userId);

        try {
            log.info("Obteniendo usuario por ID: {}", userId);
            UserDTO fetchedUser = getUserById(userId)
                    .timeout(Duration.ofSeconds(3))
                    .block();

            if (fetchedUser != null && fetchedUser.getUsername() != null) {
                userInfo = fetchedUser;
                log.info("Usuario obtenido correctamente: {}", userInfo.getUsername());
            } else {
                log.warn("No se pudo obtener información completa del usuario: {}", userId);
                userInfo.setUsername("Usuario " + userId.substring(0, 8));
                userInfo.setFirstName("Usuario");
                userInfo.setLastName(userId.substring(0, 8));
            }
        } catch (Exception e) {
            log.warn("Error al obtener información del usuario. Usando información básica. Error: {}", e.getMessage());
            userInfo.setUsername("Usuario " + userId.substring(0, 8));
            userInfo.setFirstName("Usuario");
            userInfo.setLastName(userId.substring(0, 8));
        }

        return UserMoodDTO.builder()
                .id(savedMoodUser.getId())
                .user(userInfo)
                .mood(modelMapper.map(mood, MoodResponseDto.class))
                .recordedDate(savedMoodUser.getRecordedDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserMoodDTO> getAllUsersWithMoods() {
        log.info("Obteniendo todos los usuarios con sus estados de ánimo");

        List<MoodUser> allMoodUsers = moodUserRepository.findAll();

        if (allMoodUsers.isEmpty()) {
            log.info("No se encontraron registros de estados de ánimo");
            return new ArrayList<>();
        }

        Map<String, List<MoodUser>> userMoodMap = allMoodUsers.stream()
                .collect(Collectors.groupingBy(MoodUser::getUserId));

        List<UserMoodDTO> result = new ArrayList<>();

        userMoodMap.forEach((userId, moods) -> {
            UserDTO userDTO;
            try {
                userDTO = getUserById(userId).block(Duration.ofSeconds(5));
                if (userDTO == null) {
                    userDTO = new UserDTO();
                    userDTO.setId(userId);
                    log.warn("No se pudo obtener información completa del usuario: {}", userId);
                }
            } catch (Exception e) {
                userDTO = new UserDTO();
                userDTO.setId(userId);
                log.warn("Error al obtener información del usuario {}: {}", userId, e.getMessage());
            }

            for (MoodUser moodUser : moods) {
                UserMoodDTO userMoodDTO = UserMoodDTO.builder()
                        .id(moodUser.getId())
                        .user(userDTO)
                        .mood(modelMapper.map(moodUser.getMood(), MoodResponseDto.class))
                        .recordedDate(moodUser.getRecordedDate())
                        .build();

                result.add(userMoodDTO);
            }
        });

        log.info("Se encontraron {} registros de estados de ánimo para {} usuarios",
                allMoodUsers.size(), userMoodMap.size());

        return result;
    }

    @Override
    public Mono<MoodStatisticsDto> getMoodStatistics(int months) {
        log.info("Calculando estadisticas de estados de animo para los ultimos {} meses", months);
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        return Mono.fromCallable(() -> {
            List<MoodUser> moodUsers = moodUserRepository.findByRecordedDateAfter(startDate);
            if (moodUsers.isEmpty()) {
                log.info("No se encontraron registros para el periodo especifico");
                return null;
            }
            Map<UUID, Long> moodCountById = moodUsers.stream()
                    .collect(Collectors.groupingBy(
                            mu -> mu.getMood().getId(),
                            Collectors.counting()));
            long totalMoods = moodUsers.size();
            Map<String, Long> moodCounts = new HashMap<>();
            Map<String, Double> moodPercentages = new HashMap<>();
            moodCountById.forEach((moodId, count) -> {
                MoodEntity mood = moodRepository.findById(moodId).orElse(null);
                if (mood != null) {
                    moodCounts.put(mood.getName(), count);
                    double percentage = (count * 100.0) / totalMoods;
                    moodPercentages.put(mood.getName(), Math.round(percentage * 100) / 100.0);
                }
            });
            log.info("Se analizaron {} registros de estado de animo en los ultimos {} meses", totalMoods, months);
            return MoodStatisticsDto.builder()
                    .moodCounts(moodCounts)
                    .moodPercentages(moodPercentages)
                    .totalMoods(totalMoods)
                    .timePeriod("Ultimos " + months + " meses")
                    .build();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Cacheable(value = "moodTrends", key = "'months_'+#months")
    @Override
    public Mono<MoodTrendsAnalysisDto> getMoodTrendsAnalysis(int months) {
        log.info("Calculando análisis de tendencias de estados de ánimo para los últimos {} meses", months);
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);

        return Mono.fromCallable(() -> {
            List<MoodUser> moodUsers = moodUserRepository.findByRecordedDateAfter(startDate);

            if (moodUsers.isEmpty()) {
                log.info("No se encontraron registros para el período especificado");
                return null;
            }

            moodUsers.sort(Comparator.comparing(MoodUser::getRecordedDate));

            Map<UUID, Integer> moodValues = new HashMap<>();
            List<MoodEntity> allMoods = moodRepository.findAll();

            for (MoodEntity mood : allMoods) {
                int value = switch (mood.getName().toLowerCase()) {
                    case "happy" ->
                        5;
                    case "energetic" ->
                        4;
                    case "neutral" ->
                        3;
                    case "anxious" ->
                        2;
                    case "triste",
                            "sad" ->
                        1;
                    default ->
                        3;
                };
                moodValues.put(mood.getId(), value);
            }

            // === CÁLCULO DEL ESTADO DE ÁNIMO MÁS COMÚN ===
            Map<UUID, Long> moodCountById = moodUsers.stream()
                    .collect(Collectors.groupingBy(
                            mu -> mu.getMood().getId(),
                            Collectors.counting()));

            Map.Entry<UUID, Long> mostCommonEntry = moodCountById.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);

            String mostCommonMood = "No data";
            double mostCommonPercentage = 0.0;
            long totalEntries = moodUsers.size();

            if (mostCommonEntry != null) {
                UUID moodId = mostCommonEntry.getKey();
                MoodEntity mood = moodRepository.findById(moodId).orElse(null);
                if (mood != null) {
                    mostCommonMood = mood.getName();
                    mostCommonPercentage = Math.round((mostCommonEntry.getValue() * 100.0) / totalEntries * 10) / 10.0;
                }
            }

            // === CÁLCULO DE VARIABILIDAD ===
            double variability = 0.0;
            String variabilityLevel = "No data";

            if (moodUsers.size() > 1) {
                List<Double> moodScores = new ArrayList<>();

                for (MoodUser moodUser : moodUsers) {
                    Integer moodValue = moodValues.getOrDefault(moodUser.getMood().getId(), 3);
                    moodScores.add(moodValue.doubleValue());
                }

                double mean = moodScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double variance = moodScores.stream()
                        .mapToDouble(score -> Math.pow(score - mean, 2))
                        .average().orElse(0.0);
                variability = Math.round(Math.sqrt(variance) * 10) / 10.0;

                if (variability < 0.8) {
                    variabilityLevel = "Low";
                } else if (variability < 1.5) {
                    variabilityLevel = "Moderate";
                } else {
                    variabilityLevel = "High";
                }
            }

            // === ANÁLISIS DE TENDENCIAS ===
            String trendDirection = "No data";
            double weeklyTrend = 0.0;

            if (moodUsers.size() > 1) {
                Map<Integer, List<MoodUser>> weeklyMoods = moodUsers.stream()
                        .collect(Collectors.groupingBy(mu -> {
                            LocalDateTime date = mu.getRecordedDate();
                            return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + date.getYear() * 100;
                        }));

                List<Map.Entry<Integer, Double>> weeklyScores = new ArrayList<>();

                for (Map.Entry<Integer, List<MoodUser>> entry : weeklyMoods.entrySet()) {
                    double weeklyAverage = entry.getValue().stream()
                            .mapToDouble(mu -> moodValues.getOrDefault(mu.getMood().getId(), 3))
                            .average().orElse(0.0);
                    weeklyScores.add(Map.entry(entry.getKey(), weeklyAverage));
                }

                weeklyScores.sort(Comparator.comparingInt(Map.Entry::getKey));

                if (weeklyScores.size() >= 2) {
                    double totalDiff = 0;
                    for (int i = 1; i < weeklyScores.size(); i++) {
                        totalDiff += weeklyScores.get(i).getValue() - weeklyScores.get(i - 1).getValue();
                    }
                    weeklyTrend = Math.round((totalDiff / (weeklyScores.size() - 1)) * 10) / 10.0;

                    if (weeklyTrend > 0.1) {
                        trendDirection = "Improving";
                    } else if (weeklyTrend < -0.1) {
                        trendDirection = "Declining";
                    } else {
                        trendDirection = "Stable";
                    }
                }
            }

            return MoodTrendsAnalysisDto.builder()
                    .timePeriod("Últimos " + months + " meses")
                    .totalEntries(totalEntries)
                    .mostCommonMood(mostCommonMood)
                    .mostCommonMoodPercentage(mostCommonPercentage)
                    .variabilityLevel(variabilityLevel)
                    .variabilityScore(variability)
                    .trendDirection(trendDirection)
                    .weeklyTrendScore(weeklyTrend)
                    .build();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Long countMoods() {
        log.info("Contando total de registros de estados de ánimo");
        return (long) moodUserRepository.findAll().size();
    }

    @Override
    public List<Object[]> countUsersByDayInLastMonth() {
        log.info("Contando usuarios que registraron su estado de ánimo por día en el último mes");

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);

        log.debug("Consultando registros entre {} y {}", startDate, endDate);

        try {
            List<Object[]> results = moodUserRepository.countDistinctUsersByDayBetweenDates(startDate, endDate);
            log.info("Encontrados registros para {} días diferentes", results.size());
            return results;
        } catch (Exception e) {
            log.error("Error al contar usuarios por día: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public Long countMoodsPreviousMonth() {
        log.info("Contando registros de estados de ánimo del mes anterior");
        LocalDateTime startOfPreviousMonth = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0)
                .withMinute(0).withSecond(0);
        LocalDateTime endOfPreviousMonth = startOfPreviousMonth.plusMonths(1).minusSeconds(1);

        return moodUserRepository.findAll().stream()
                .filter(mood -> {
                    LocalDateTime date = mood.getRecordedDate();
                    return date.isAfter(startOfPreviousMonth) && date.isBefore(endOfPreviousMonth);
                })
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countMoodLogDays(String userId) {
        log.info("Contando los días de registro de estado de ánimo para el usuario: {}", userId);
        return moodUserRepository.countDistinctDaysByUserId(userId);
    }

    @Override
    public Double getAverageMood(String userId) {
        log.info("Calculando el promedio de estados de ánimo para el usuario: {}", userId);
        Double average = moodUserRepository.getAverageMoodValueByUserId(userId);
        if (average == null) {
            return null;
        }
        return Math.round(average * 10.0) / 10.0;
    }


}