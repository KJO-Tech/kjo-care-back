package kjo.care.msvc_moodTracking.mappers;

import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodResponseDto;
import kjo.care.msvc_moodTracking.Entities.MoodEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MoodMapper {
    private final ModelMapper modelMapper;

    public MoodMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.getConfiguration().setSkipNullEnabled(true);
    }

    public MoodResponseDto entityToDto(MoodEntity entity) {
        return modelMapper.map(entity, MoodResponseDto.class);
    }

    public MoodEntity dtoToEntity(MoodRequestDto dto) {
        return modelMapper.map(dto, MoodEntity.class);
    }
}
