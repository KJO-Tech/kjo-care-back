package kjo.care.msvc_dailyActivity.Services.Impl;

import kjo.care.msvc_dailyActivity.DTOs.SubscriptionResponseDTO;
import kjo.care.msvc_dailyActivity.Entities.Category;
import kjo.care.msvc_dailyActivity.Entities.UserCategorySubscription;
import kjo.care.msvc_dailyActivity.Exceptions.ResourceNotFoundException;
import kjo.care.msvc_dailyActivity.Mappers.SubscriptionMapper;
import kjo.care.msvc_dailyActivity.Repositories.CategoryRepository;
import kjo.care.msvc_dailyActivity.Repositories.UserCategorySubscriptionRepository;
import kjo.care.msvc_dailyActivity.Services.ISubscriptionService;
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
public class SubscriptionServiceImpl implements ISubscriptionService {

    private final UserCategorySubscriptionRepository subscriptionRepository;
    private final CategoryRepository categoryRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    @Transactional
    public SubscriptionResponseDTO subscribe(String userId, UUID categoryId) {
        log.info("Usuario {} intentando suscribirse a categoría {}", userId, categoryId);

        // Verificar que la categoría existe
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", categoryId));

        // Verificar si ya está suscrito
        if (subscriptionRepository.existsByUserIdAndCategoryId(userId, categoryId)) {
            throw new IllegalArgumentException("Ya estás suscrito a esta categoría");
        }

        // Crear la suscripción
        UserCategorySubscription subscription = subscriptionMapper.toEntity(userId, category);
        UserCategorySubscription savedSubscription = subscriptionRepository.save(subscription);

        log.info("Usuario {} suscrito exitosamente a categoría {}", userId, categoryId);
        return subscriptionMapper.toResponseDTO(savedSubscription);
    }

    @Override
    @Transactional
    public void unsubscribe(String userId, UUID categoryId) {
        log.info("Usuario {} intentando desuscribirse de categoría {}", userId, categoryId);

        // Verificar que la categoría existe
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Categoría", "id", categoryId);
        }

        // Verificar que está suscrito
        if (!subscriptionRepository.existsByUserIdAndCategoryId(userId, categoryId)) {
            throw new IllegalArgumentException("No estás suscrito a esta categoría");
        }

        // Eliminar la suscripción
        subscriptionRepository.deleteByUserIdAndCategoryId(userId, categoryId);
        log.info("Usuario {} desuscrito exitosamente de categoría {}", userId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponseDTO> getMySubscriptions(String userId) {
        log.info("Obteniendo suscripciones del usuario {}", userId);

        return subscriptionRepository.findByUserId(userId)
                .stream()
                .map(subscriptionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSubscribed(String userId, UUID categoryId) {
        return subscriptionRepository.existsByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countSubscriptionsByUser(String userId) {
        return subscriptionRepository.countByUserId(userId);
    }
}