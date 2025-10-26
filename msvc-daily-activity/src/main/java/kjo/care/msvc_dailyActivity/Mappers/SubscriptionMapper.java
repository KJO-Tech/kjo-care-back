package kjo.care.msvc_dailyActivity.Mappers;

import kjo.care.msvc_dailyActivity.DTOs.SubscriptionResponseDTO;
import kjo.care.msvc_dailyActivity.Entities.Category;
import kjo.care.msvc_dailyActivity.Entities.UserCategorySubscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public UserCategorySubscription toEntity(String userId, Category category) {
        if (userId == null || category == null) {
            return null;
        }

        return UserCategorySubscription.builder()
                .userId(userId)
                .category(category)
                .build();
    }

    public SubscriptionResponseDTO toResponseDTO(UserCategorySubscription entity) {
        if (entity == null) {
            return null;
        }

        return SubscriptionResponseDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .categoryDescription(entity.getCategory() != null ? entity.getCategory().getDescription() : null)
                .categoryImageUrl(entity.getCategory() != null ? entity.getCategory().getImageUrl() : null)
                .subscribedAt(entity.getSubscribedAt())
                .build();
    }
}
