package kjo.care.msvc_emergency.mappers;

import jakarta.annotation.PostConstruct;
import kjo.care.msvc_emergency.client.UserClient;
import kjo.care.msvc_emergency.dto.EmergencyRequestDto;
import kjo.care.msvc_emergency.dto.EmergencyResponseDto;
import kjo.care.msvc_emergency.dto.HealthResponseDto;
import kjo.care.msvc_emergency.dto.UserInfoDto;
import kjo.care.msvc_emergency.entities.EmergencyResource;
import kjo.care.msvc_emergency.entities.HealthCenter;
import kjo.care.msvc_emergency.repositories.EmergencyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EmergencyMapper {

    private final ModelMapper modelMapper;
    private final EmergencyRepository emergencyRepository;
    private final UserClient userClient;

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        TypeMap<EmergencyRequestDto, EmergencyResource> requestMap = modelMapper.createTypeMap(EmergencyRequestDto.class, EmergencyResource.class);
        requestMap.addMappings(mapper -> {
            mapper.skip(EmergencyResource::setId);
            mapper.skip(EmergencyResource::setCreatedDate);
            mapper.skip(EmergencyResource::setModifiedDate);
            mapper.skip(EmergencyResource::setStatus);
        });
    }

    public EmergencyResponseDto entityToDto(EmergencyResource entity) {
        UserInfoDto user = userClient.findUserById(entity.getUserId());
        EmergencyResponseDto dto = modelMapper.map(entity, EmergencyResponseDto.class);
        dto.setUser(user);
        return dto;
    }

    public EmergencyResource dtoToEntity(EmergencyRequestDto dto) {
        EmergencyResource emergency = modelMapper.map(dto, EmergencyResource.class);
        return emergency;
    }

    public void updateEntityFromDto(EmergencyRequestDto dto, EmergencyResource entity) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.typeMap(EmergencyRequestDto.class, EmergencyResource.class)
                .addMappings(mapper -> {
                    mapper.skip(EmergencyResource::setResourceUrl);
                });
        modelMapper.map(dto, entity);
        entity.setModifiedDate(LocalDate.now());
    }

    public List<EmergencyResponseDto> entitiesToDtos(List<EmergencyResource> entities) {
        List<String> userIds = entities.stream()
                .map(EmergencyResource::getUserId)
                .distinct()
                .toList();

        List<UserInfoDto> users = userClient.findUsersByIds(userIds);

        return entities.stream()
                .map(entity -> {
                    EmergencyResponseDto dto = modelMapper.map(entity, EmergencyResponseDto.class);
                    users.stream()
                            .filter(user -> user.getId().equals(entity.getUserId()))
                            .findFirst()
                            .ifPresent(dto::setUser);
                    return dto;
                })
                .toList();
    }
}
