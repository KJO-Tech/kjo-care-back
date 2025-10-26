package kjo.care.msvc_moodTracking.services;

import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodPageResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.UserMoodDTO;

import java.util.List;
import java.util.UUID;

public interface MoodService {
    MoodPageResponseDto findAllMoods(int page, int size);

    MoodResponseDto findMoodById(UUID id);

    MoodResponseDto saveMood(MoodRequestDto dto);

    MoodResponseDto updateMood(UUID id, MoodRequestDto dto);

    void deleteMood(UUID id);

    MoodResponseDto toggleMoodStatus(UUID id);
}
