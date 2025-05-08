package kjo.care.msvc_emergency.services.impl;

import kjo.care.msvc_emergency.client.UserClient;
import kjo.care.msvc_emergency.dto.EmergencyRequestDto;
import kjo.care.msvc_emergency.dto.EmergencyResponseDto;
import kjo.care.msvc_emergency.dto.StatsResponseDto;
import kjo.care.msvc_emergency.dto.UserInfoDto;
import kjo.care.msvc_emergency.entities.EmergencyResource;
import kjo.care.msvc_emergency.enums.StatusEmergency;
import kjo.care.msvc_emergency.exceptions.EntityNotFoundException;
import kjo.care.msvc_emergency.mappers.EmergencyMapper;
import kjo.care.msvc_emergency.repositories.EmergencyRepository;
import kjo.care.msvc_emergency.services.EmergencyService;
import kjo.care.msvc_emergency.services.IUploadImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Service
@Log4j2
@Validated
@RequiredArgsConstructor
public class EmergencyServiceImpl implements EmergencyService {

    private final EmergencyRepository emergencyRepository;
    private final EmergencyMapper emergencyMapper;
    private final UserClient userClient;
    private final IUploadImageService uploadService;

    @Override
    public List<EmergencyResponseDto> findAll() {
        List<EmergencyResource> entities = emergencyRepository.findAll();
        return emergencyMapper.entitiesToDtos(entities);
    }

    @Override
    public List<EmergencyResponseDto> findAllActive() {
        List<EmergencyResource> activeEntities = emergencyRepository.findAll().stream()
                .filter(emergency -> emergency.getStatus().equals(StatusEmergency.ACTIVE))
                .toList();
        return emergencyMapper.entitiesToDtos(activeEntities);
    }

    @Override
    public StatsResponseDto setStats() {
        int totalResources = emergencyRepository.countAllEmergencies();
        int activeEmergencies = emergencyRepository.countActiveEmergencies();
        int totalContacts = emergencyRepository.sumAllContacts();
        int totalLinks = emergencyRepository.sumAllLinks();
        int totalAccesses = emergencyRepository.sumTotalAccesses();

        return StatsResponseDto.builder()
                .totalResources(totalResources)
                .activeEmergencies(activeEmergencies)
                .totalContacts(totalContacts)
                .totalLinks(totalLinks)
                .totalAccesses(totalAccesses)
                .build();
    }

    @Override
    public EmergencyResponseDto findById(Long id) {
        emergencyRepository.incrementAccessCount(id);
        EmergencyResource emergencyResource = findExistEmergencyResource(id);
        boolean isAdmin = isAdminFromJwt();
        boolean isActive = emergencyResource.getStatus().equals(StatusEmergency.ACTIVE);

        if (!isAdmin && !isActive) {
            throw new AccessDeniedException("Acceso denegado: El recurso de emergencia no está activo");
        }

        return emergencyMapper.entityToDto(emergencyResource);
    }

    @Override
    public EmergencyResponseDto save(EmergencyRequestDto dto, String userId) {
        UserInfoDto user = userClient.findUserById(userId);
        EmergencyResource emergencyResource = emergencyMapper.dtoToEntity(dto);
        saveImageOrVideo(dto, emergencyResource);
        emergencyResource.setUserId(userId);
        emergencyResource.setStatus(StatusEmergency.ACTIVE);
        emergencyResource.setAccessCount(1);
        emergencyRepository.save(emergencyResource);
        return emergencyMapper.entityToDto(emergencyResource);
    }

    @Override
    public EmergencyResponseDto update(Long id, EmergencyRequestDto dto, String userId) {
        EmergencyResource emergencyResource = findExistEmergencyResource(id);

        boolean isAdmin = isAdminFromJwt();

        if (!isAdmin && !emergencyResource.getUserId().equals(userId)) {
            throw new AccessDeniedException("Acción no permitida");
        }

        emergencyMapper.updateEntityFromDto(dto, emergencyResource);
        deleteImageOrVideo(emergencyResource);
        saveImageOrVideo(dto, emergencyResource);
        emergencyRepository.save(emergencyResource);
        return emergencyMapper.entityToDto(emergencyResource);
    }

    @Override
    public void delete(Long id, String userId) {
        UserInfoDto user = userClient.findUserById(userId);
        EmergencyResource emergencyResource = findExistEmergencyResource(id);
        boolean isAdmin = isAdminFromJwt();

        if (!isAdmin && !emergencyResource.getUserId().equals(userId)) {
            throw new AccessDeniedException("Acción no permitida");
        }

        emergencyResource.setStatus(StatusEmergency.INACTIVE);
        emergencyRepository.save(emergencyResource);
    }

    private EmergencyResource findExistEmergencyResource(Long id) {
        return emergencyRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Recurso de emergencia con id :" + id + " no encontrado");
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

    private void saveImageOrVideo(EmergencyRequestDto dto, EmergencyResource emergencyResource) {
        if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            String imageUrl = uploadService.uploadFile(
                    dto.getImageUrl(),
                    "emergency/images",
                    "auto"
            );
            emergencyResource.setResourceUrl(imageUrl);
        }
        if (dto.getVideoUrl() != null && !dto.getVideoUrl().isEmpty()) {
            String videoUrl = uploadService.uploadFile(
                    dto.getVideoUrl(),
                    "emergency/videos",
                    "video"
            );
            emergencyResource.setResourceUrl(videoUrl);
        }
    }

    private void deleteImageOrVideo(EmergencyResource emergencyResource) {
        try {
            String resourceUrl = emergencyResource.getResourceUrl();
            if (resourceUrl != null && resourceUrl.startsWith("http")) {
                String resourceType = "image";

                if (resourceUrl.contains("/blog/videos/")) {
                    resourceType = "video";
                } else if (resourceUrl.contains("/blog/images/")) {
                    resourceType = "image";
                }
                uploadService.DeleteImage(resourceUrl, resourceType);
            }
        } catch (Exception e) {
            log.error("Error al eliminar recurso: {}", e.getMessage());
        }
    }

}
