package kjo.care.msvc_emergency.services;

import kjo.care.msvc_emergency.dto.EmergencyRequestDto;
import kjo.care.msvc_emergency.dto.EmergencyResponseDto;
import kjo.care.msvc_emergency.dto.HealthRequestDto;
import kjo.care.msvc_emergency.dto.HealthResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HealthService {
    Page<HealthResponseDto> findAll(int page, int size, String search);
    List<HealthResponseDto> findAllActive();
    List<HealthResponseDto> findNearby(double lat, double lon, double distanceKm);
    HealthResponseDto findById(UUID id);
    HealthResponseDto save(HealthRequestDto dto, String userId);
    HealthResponseDto update (UUID id , HealthRequestDto dto, String userId);
    void delete(UUID id,  String userId);
    int countTotalHealthCenters();
    int countActiveHealthCenters();
    int countPreviousMonthHealthCenters();
}
