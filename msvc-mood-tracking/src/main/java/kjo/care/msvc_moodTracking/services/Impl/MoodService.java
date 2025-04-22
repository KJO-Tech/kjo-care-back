package kjo.care.msvc_moodTracking.services.Impl;

import kjo.care.msvc_moodTracking.DTOs.MoodPageResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodResponseDto;

public interface MoodService {
    MoodPageResponseDto findAllMoods(int page, int size);

    MoodResponseDto findMoodById(Long id);

    MoodResponseDto saveMood(MoodRequestDto dto);
    MoodResponseDto updateMood (Long id , MoodRequestDto dto);
    public void deleteMood(Long id);
    MoodResponseDto toggleMoodStatus(Long id);
}
