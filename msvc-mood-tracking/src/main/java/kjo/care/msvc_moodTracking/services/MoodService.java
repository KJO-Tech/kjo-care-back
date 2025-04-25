package kjo.care.msvc_moodTracking.services;

import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodPageResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.UserMoodDTO;

import java.util.List;

public interface MoodService {
    MoodPageResponseDto findAllMoods(int page, int size);

    MoodResponseDto findMoodById(Long id);

    MoodResponseDto saveMood(MoodRequestDto dto);

    MoodResponseDto updateMood(Long id, MoodRequestDto dto);

    void deleteMood(Long id);

    MoodResponseDto toggleMoodStatus(Long id);
}
