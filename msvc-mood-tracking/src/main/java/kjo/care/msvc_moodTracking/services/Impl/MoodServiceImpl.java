package kjo.care.msvc_moodTracking.services.Impl;

import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodPageResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodResponseDto;
import kjo.care.msvc_moodTracking.Entities.MoodEntity;
import kjo.care.msvc_moodTracking.Repositories.MoodRepository;
import kjo.care.msvc_moodTracking.exceptions.MoodEntityNotFoundException;
import kjo.care.msvc_moodTracking.mappers.MoodMapper;
import kjo.care.msvc_moodTracking.services.MoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Validated
public class MoodServiceImpl implements MoodService {
    private final MoodRepository moodRepository;
    private final MoodMapper moodMapper;

    @Override
    @Transactional(readOnly = true)
    public MoodPageResponseDto findAllMoods(int page, int size) {
        log.info("Iniciando findAllMoods con page:{} y size {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<MoodEntity> moodPage = moodRepository.findAll(pageable);
        List<MoodResponseDto> moodDtos = moodPage.getContent().stream().map(
                moodMapper::entityToDto
        ).collect(Collectors.toList());
        return new MoodPageResponseDto(
                moodDtos, moodPage.getTotalPages(), moodPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "moods", key = "#id")
    public MoodResponseDto findMoodById(Long id) {
        log.info("Buscando mood con id {}", id);
        MoodEntity mood = findExistMoodById(id);
        MoodResponseDto response = moodMapper.entityToDto(mood);
        log.info("Mood encontrado con id : {}", id);
        return response;
    }

    @Transactional
    @Override
    public MoodResponseDto saveMood(MoodRequestDto dto) {
        log.info("Guardando un Mood");
        MoodEntity mood = moodMapper.dtoToEntity(dto);
        moodRepository.save(mood);
        return moodMapper.entityToDto(mood);
    }

    @Transactional
    @Override
    public MoodResponseDto updateMood(Long id, MoodRequestDto dto) {
        log.info("Actualizando mood con id {}", id);
        MoodEntity mood = findExistMoodById(id);
        moodMapper.updateEntityFromDto(dto, mood);
        moodRepository.save(mood);
        log.info("Mood actualizado con id {}", id);
        return moodMapper.entityToDto(mood);
    }

    @Transactional
    @Override
    public void deleteMood(Long id) {
        MoodEntity mood = findExistMoodById(id);
        moodRepository.delete(mood);
        log.info("Mood eliminado : id {}", id);
    }

    @Override
    public MoodResponseDto toggleMoodStatus(Long id) {
        log.info("Cambiando estado activo del mood con id {}", id);
        MoodEntity mood = findExistMoodById(id);
        mood.setIsActive(!mood.getIsActive());
        moodRepository.save(mood);
        log.info("Nuevo estado de Mood {} , con id {}", mood.getIsActive(), id);
        return moodMapper.entityToDto(mood);
    }

    private MoodEntity findExistMoodById(Long id) {
        return moodRepository.findById(id).orElseThrow(() -> {
            log.warn("Teacher con id {} no econtrado", id);
            return new MoodEntityNotFoundException("Mood con id :" + id + " no encontrado");
        });
    }

}
