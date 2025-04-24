package kjo.care.msvc_moodTracking.services;

import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.MoodUserRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.UserDTO;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.UserMoodDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MoodUserService {

    Mono<UserDTO> getUserById(String id);

    Flux<UserMoodDTO> getCurrentUserMoods(String userId);

    UserMoodDTO trackUserMood(String userId, MoodUserRequestDto moodUserRequestDto);

    List<UserMoodDTO> getAllUsersWithMoods();

}