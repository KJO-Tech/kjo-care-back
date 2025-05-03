package kjo.care.msvc_emergency.services;

import kjo.care.msvc_emergency.dto.EmergencyRequestDto;
import kjo.care.msvc_emergency.dto.EmergencyResponseDto;
import kjo.care.msvc_emergency.dto.HealthRequestDto;
import kjo.care.msvc_emergency.dto.HealthResponseDto;

import java.util.List;

public interface HealthService {
    List<HealthResponseDto> findAll();
    List<HealthResponseDto> findAllActive();
    HealthResponseDto findById(Long id);
    HealthResponseDto save(HealthRequestDto dto, String userId);
    HealthResponseDto update (Long id , HealthRequestDto dto, String userId);
    void delete(Long id,  String userId);
}
