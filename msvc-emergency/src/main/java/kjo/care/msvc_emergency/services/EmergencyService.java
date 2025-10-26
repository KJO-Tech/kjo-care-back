package kjo.care.msvc_emergency.services;

import kjo.care.msvc_emergency.dto.EmergencyRequestDto;
import kjo.care.msvc_emergency.dto.EmergencyResponseDto;
import kjo.care.msvc_emergency.dto.StatsResponseDto;

import java.util.List;
import java.util.UUID;

public interface EmergencyService {
    List<EmergencyResponseDto> findAll();
    List<EmergencyResponseDto> findAllActive();
    StatsResponseDto setStats();
    EmergencyResponseDto findById(UUID id);
    EmergencyResponseDto save(EmergencyRequestDto dto, String userId);
    EmergencyResponseDto update (UUID id , EmergencyRequestDto dto, String userId);
    void delete(UUID id,  String userId);
}
