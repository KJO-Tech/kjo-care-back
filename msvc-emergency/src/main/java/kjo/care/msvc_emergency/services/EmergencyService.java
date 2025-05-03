package kjo.care.msvc_emergency.services;

import kjo.care.msvc_emergency.dto.EmergencyRequestDto;
import kjo.care.msvc_emergency.dto.EmergencyResponseDto;
import kjo.care.msvc_emergency.dto.StatsResponseDto;

import java.util.List;

public interface EmergencyService {
    List<EmergencyResponseDto> findAll();
    List<EmergencyResponseDto> findAllActive();
    StatsResponseDto setStats();
    EmergencyResponseDto findById(Long id);
    EmergencyResponseDto save(EmergencyRequestDto dto, String userId);
    EmergencyResponseDto update (Long id , EmergencyRequestDto dto, String userId);
    void delete(Long id,  String userId);
}
