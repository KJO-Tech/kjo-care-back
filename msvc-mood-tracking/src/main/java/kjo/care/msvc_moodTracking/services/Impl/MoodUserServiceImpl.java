package kjo.care.msvc_moodTracking.services.Impl;


import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.MoodStatisticsDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.MoodUserRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.UserDTO;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.UserMoodDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return Mono.fromCallable(() -> moodUserRepository.findByUserId(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .flatMap(moodUser -> getUserById(userId)
                        .map(userDTO -> UserMoodDTO.builder()
                                .id(moodUser.getId())
                                .user(userDTO)
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
                        })
                );
    }


    @Transactional
    @Override
    public UserMoodDTO trackUserMood(String userId, MoodUserRequestDto moodUserRequestDto) {
        log.info("Registrando estado de ánimo para usuario: {}", userId);

        MoodEntity mood = moodRepository.findById(moodUserRequestDto.moodId())
                .orElseThrow(() -> new MoodEntityNotFoundException("Estado de ánimo no encontrado: " + moodUserRequestDto.moodId()));

        if (!mood.getIsActive()) {
            throw new IllegalArgumentException("Este estado de ánimo no está disponible para selección");
        }

        MoodUser moodUser = MoodUser.builder()
                .userId(userId)
                .mood(mood)
                .recordedDate(LocalDateTime.now())
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
        } catch (
                Exception e) {
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
            } catch (
                    Exception e) {
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
            Map<Long, Long> moodCountById = moodUsers.stream()
                    .collect(Collectors.groupingBy(
                            mu -> mu.getMood().getId(),
                            Collectors.counting()
                    ));
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

}