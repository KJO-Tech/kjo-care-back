package kjo.care.msvc_dailyActivity.Services.Impl;

import kjo.care.msvc_dailyActivity.Services.IDailyExerciseService;
import kjo.care.msvc_dailyActivity.DTOs.DailyExerciseRequestDTO;
import kjo.care.msvc_dailyActivity.DTOs.DailyExerciseResponseDTO;
import kjo.care.msvc_dailyActivity.Entities.Category;
import kjo.care.msvc_dailyActivity.Entities.DailyExercise;
import kjo.care.msvc_dailyActivity.Enums.ExerciseContentType;
import kjo.care.msvc_dailyActivity.Enums.ExerciseDifficultyType;
import kjo.care.msvc_dailyActivity.Exceptions.ResourceNotFoundException;
import kjo.care.msvc_dailyActivity.Mappers.DailyExerciseMapper;
import kjo.care.msvc_dailyActivity.Repositories.CategoryRepository;
import kjo.care.msvc_dailyActivity.Repositories.DailyExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyExerciseServiceImpl implements IDailyExerciseService {

    private final DailyExerciseRepository dailyExerciseRepository;
    private final CategoryRepository categoryRepository;
    private final DailyExerciseMapper dailyExerciseMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DailyExerciseResponseDTO> getAllExercises() {
        log.info("Obteniendo todos los ejercicios diarios");
        return dailyExerciseRepository.findAll()
                .stream()
                .map(dailyExerciseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DailyExerciseResponseDTO getExerciseById(UUID id) {
        log.info("Obteniendo ejercicio con ID: {}", id);
        DailyExercise exercise = dailyExerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio", "id", id));
        return dailyExerciseMapper.toResponseDTO(exercise);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyExerciseResponseDTO> getExercisesByCategory(UUID categoryId) {
        log.info("Obteniendo ejercicios por categoría ID: {}", categoryId);
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Categoría", "id", categoryId);
        }
        return dailyExerciseRepository.findByCategoryId(categoryId)
                .stream()
                .map(dailyExerciseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyExerciseResponseDTO> getExercisesByContentType(ExerciseContentType contentType) {
        log.info("Obteniendo ejercicios por tipo de contenido: {}", contentType);
        return dailyExerciseRepository.findByContentType(contentType)
                .stream()
                .map(dailyExerciseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyExerciseResponseDTO> getExercisesByDifficulty(ExerciseDifficultyType difficulty) {
        log.info("Obteniendo ejercicios por dificultad: {}", difficulty);
        return dailyExerciseRepository.findByDifficulty(difficulty)
                .stream()
                .map(dailyExerciseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyExerciseResponseDTO> getExercisesByCategoryAndDifficulty(UUID categoryId, ExerciseDifficultyType difficulty) {
        log.info("Obteniendo ejercicios por categoría {} y dificultad {}", categoryId, difficulty);
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Categoría", "id", categoryId);
        }
        return dailyExerciseRepository.findByCategoryAndDifficulty(categoryId, difficulty)
                .stream()
                .map(dailyExerciseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DailyExerciseResponseDTO createExercise(DailyExerciseRequestDTO requestDTO) {
        log.info("Creando nuevo ejercicio: {}", requestDTO.getTitle());
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", requestDTO.getCategoryId()));
        DailyExercise exercise = dailyExerciseMapper.toEntity(requestDTO, category);
        DailyExercise savedExercise = dailyExerciseRepository.save(exercise);
        log.info("Ejercicio creado exitosamente con ID: {}", savedExercise.getId());
        return dailyExerciseMapper.toResponseDTO(savedExercise);
    }

    @Override
    @Transactional
    public DailyExerciseResponseDTO updateExercise(UUID id, DailyExerciseRequestDTO requestDTO) {
        log.info("Actualizando ejercicio con ID: {}", id);
        DailyExercise exercise = dailyExerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio", "id", id));
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", requestDTO.getCategoryId()));
        dailyExerciseMapper.updateEntityFromDTO(requestDTO, exercise, category);
        DailyExercise updatedExercise = dailyExerciseRepository.save(exercise);
        log.info("Ejercicio actualizado exitosamente con ID: {}", id);
        return dailyExerciseMapper.toResponseDTO(updatedExercise);
    }

    @Override
    @Transactional
    public void deleteExercise(UUID id) {
        log.info("Eliminando ejercicio con ID: {}", id);
        DailyExercise exercise = dailyExerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio", "id", id));
        dailyExerciseRepository.delete(exercise);
        log.info("Ejercicio eliminado exitosamente con ID: {}", id);
    }
}
