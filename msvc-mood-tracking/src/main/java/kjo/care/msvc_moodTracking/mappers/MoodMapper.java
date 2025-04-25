package kjo.care.msvc_moodTracking.mappers;

import kjo.care.msvc_moodTracking.DTOs.MoodRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodResponseDto;
import kjo.care.msvc_moodTracking.Entities.MoodEntity;
import kjo.care.msvc_moodTracking.enums.MoodState;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MoodMapper {
    private final ModelMapper modelMapper;

    public MoodMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.createTypeMap(MoodRequestDto.class, MoodEntity.class)
                .addMappings(mapper -> {
                    mapper.skip(MoodEntity::setState);
                });
    }

    public MoodResponseDto entityToDto(MoodEntity entity) {
        return modelMapper.map(entity, MoodResponseDto.class);
    }

    public MoodEntity dtoToEntity(MoodRequestDto dto) {
        MoodEntity entity = modelMapper.map(dto, MoodEntity.class);
        try {
            entity.setState(MoodState.valueOf(dto.getState()));
        } catch (
                IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de animo invalido:" + dto.getState());
        }
        return entity;
    }

    public void updateEntityFromDto(MoodRequestDto dto, MoodEntity entity) {
        modelMapper.map(dto, entity);
        if (dto.getState() != null) {
            try {
                entity.setState(MoodState.valueOf(dto.getState()));
            } catch (
                    IllegalArgumentException e) {
                throw new IllegalArgumentException("Estado de animo invalido" + dto.getState());
            }
        }
    }
}
