package kjo.care.msvc_emergency.mappers;

import jakarta.annotation.PostConstruct;
import kjo.care.msvc_emergency.client.UserClient;
import kjo.care.msvc_emergency.dto.*;
import kjo.care.msvc_emergency.entities.EmergencyResource;
import kjo.care.msvc_emergency.entities.HealthCenter;
import kjo.care.msvc_emergency.repositories.HealthRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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
}
