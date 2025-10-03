package kjo.care.msvc_emergency.mappers;

import jakarta.annotation.PostConstruct;
import kjo.care.msvc_emergency.client.UserClient;
import kjo.care.msvc_emergency.dto.*;
import kjo.care.msvc_emergency.entities.HealthCenter;
import kjo.care.msvc_emergency.repositories.HealthRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HealthMapper {

    private final ModelMapper modelMapper;
    private final HealthRepository healthRepository;
    private final UserClient userClient;

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        TypeMap<HealthRequestDto, HealthCenter> requestMap = modelMapper.createTypeMap(HealthRequestDto.class, HealthCenter.class);
        requestMap.addMappings(mapper -> {
            mapper.skip(HealthCenter::setId);
            mapper.skip(HealthCenter::setCreatedDate);
            mapper.skip(HealthCenter::setModifiedDate);
            mapper.skip(HealthCenter::setStatus);
        });
    }

    public HealthResponseDto entityToDto(HealthCenter entity) {
        UserInfoDto user = userClient.findUserById(entity.getUserId());
        HealthResponseDto dto = modelMapper.map(entity, HealthResponseDto.class);
        dto.setUser(user);
        return dto;
    }

    public HealthCenter dtoToEntity(HealthRequestDto dto) {
        HealthCenter healthCenter = modelMapper.map(dto, HealthCenter.class);
        return healthCenter;
    }

    public void updateEntityFromDto(HealthRequestDto dto, HealthCenter entity) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, entity);
        entity.setModifiedDate(LocalDate.now());
    }

    public List<HealthResponseDto> entitiesToDtos(List<HealthCenter> entities) {
        List<String> userIds = entities.stream()
                .map(HealthCenter::getUserId)
                .distinct()
                .toList();

        List<UserInfoDto> users = userClient.findUsersByIds(userIds);

        return entities.stream()
                .map(entity -> {
                    HealthResponseDto dto = modelMapper.map(entity, HealthResponseDto.class);
                    users.stream()
                            .filter(user -> user.getId().equals(entity.getUserId()))
                            .findFirst()
                            .ifPresent(dto::setUser);
                    return dto;
                })
                .toList();
    }

        public static HealthResponseDto toDto(HealthCenter entity) {
            HealthResponseDto dto = new HealthResponseDto();
            dto.setId(entity.getId());
            dto.setName(entity.getName());
            dto.setLatitude(entity.getLatitude());
            dto.setLongitude(entity.getLongitude());
                dto.setStatus(entity.getStatus().name());
            return dto;
        }
}
