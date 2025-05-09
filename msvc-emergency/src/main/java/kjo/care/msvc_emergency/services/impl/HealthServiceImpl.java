package kjo.care.msvc_emergency.services.impl;

import kjo.care.msvc_emergency.client.UserClient;
import kjo.care.msvc_emergency.dto.HealthRequestDto;
import kjo.care.msvc_emergency.dto.HealthResponseDto;
import kjo.care.msvc_emergency.dto.UserInfoDto;
import kjo.care.msvc_emergency.entities.EmergencyResource;
import kjo.care.msvc_emergency.entities.HealthCenter;
import kjo.care.msvc_emergency.enums.StatusHealth;
import kjo.care.msvc_emergency.exceptions.EntityNotFoundException;
import kjo.care.msvc_emergency.mappers.EmergencyMapper;
import kjo.care.msvc_emergency.mappers.HealthMapper;
import kjo.care.msvc_emergency.repositories.EmergencyRepository;
import kjo.care.msvc_emergency.repositories.HealthRepository;
import kjo.care.msvc_emergency.services.HealthService;
import kjo.care.msvc_emergency.services.IUploadImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@Validated
@RequiredArgsConstructor
public class HealthServiceImpl implements HealthService {

    private final HealthRepository healthRepository;
    private final HealthMapper healthMapper;
    private final UserClient userClient;
    private final IUploadImageService uploadService;

    @Override
    public List<HealthResponseDto> findAll() {
        return healthRepository.findAll().stream().map(healthMapper::entityToDto).toList();
    }

    @Override
    public List<HealthResponseDto> findAllActive() {
        return healthRepository.findAll().stream().filter(health -> health.getStatus().equals(StatusHealth.ACTIVE)).map(healthMapper::entityToDto).toList();
    }

    @Override
    public HealthResponseDto findById(Long id) {
        HealthCenter healthCenter = findHealthCenter(id);
        boolean isAdmin = isAdminFromJwt();
        boolean isActive = healthCenter.getStatus().equals(StatusHealth.ACTIVE);

        if (!isAdmin && !isActive) {
            throw new AccessDeniedException("Acceso denegado: El centro de salud no está activo");
        }

        return  healthMapper.entityToDto(healthCenter);
    }

    @Override
    public HealthResponseDto save(HealthRequestDto dto, String userId) {
        UserInfoDto user = userClient.findUserById(userId);
        HealthCenter healthCenter = healthMapper.dtoToEntity(dto);
        healthCenter.setUserId(userId);
        healthCenter.setStatus(StatusHealth.ACTIVE);
        healthRepository.save(healthCenter);
        return healthMapper.entityToDto(healthCenter);
    }

    @Override
    public HealthResponseDto update(Long id, HealthRequestDto dto, String userId) {
        HealthCenter healthCenter = findHealthCenter(id);

        boolean isAdmin = isAdminFromJwt();

        if (!isAdmin && !healthCenter.getUserId().equals(userId)) {
            throw new AccessDeniedException("Acción no permitida");
        }

        healthMapper.updateEntityFromDto(dto, healthCenter);
        healthRepository.save(healthCenter);
        return healthMapper.entityToDto(healthCenter);
    }

    @Override
    public void delete(Long id, String userId) {
        HealthCenter healthCenter = findHealthCenter(id);
        boolean isAdmin = isAdminFromJwt();
        if (!isAdmin && !healthCenter.getUserId().equals(userId)) {
            throw new AccessDeniedException("Acción no permitida");
        }
        healthCenter.setStatus(StatusHealth.INACTIVE);
        healthRepository.save(healthCenter);
    }

    @Override
    public int countTotalHealthCenters() {
        return (int) healthRepository.count();
    }

    @Override
    public int countActiveHealthCenters() {
        return (int) healthRepository.countByStatus(StatusHealth.ACTIVE);
    }

    @Override
    public int countPreviousMonthHealthCenters() {
        // Definimos el inicio y fin del mes anterior
        LocalDate now = LocalDate.now();
        LocalDate startOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate endOfPreviousMonth = startOfPreviousMonth.plusMonths(1).minusDays(1);

        return healthRepository.countByCreatedDateBetween(startOfPreviousMonth, endOfPreviousMonth);
    }

    private HealthCenter findHealthCenter(Long id) {
        return healthRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Centro de salud con id :" + id + " no encontrado");
        });
    }

    private boolean isAdminFromJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("kjo-care-client");
                if (clientAccess != null) {
                    List<String> clientRoles = (List<String>) clientAccess.get("roles");
                    return clientRoles != null && clientRoles.contains("admin_client_role");
                }
            }
        }
        return false;
    }
}
