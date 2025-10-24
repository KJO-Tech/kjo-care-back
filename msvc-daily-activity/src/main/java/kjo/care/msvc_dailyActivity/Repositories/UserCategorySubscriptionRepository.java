package kjo.care.msvc_dailyActivity.Repositories;

import kjo.care.msvc_dailyActivity.Entities.UserCategorySubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCategorySubscriptionRepository extends JpaRepository<UserCategorySubscription, UUID> {

    List<UserCategorySubscription> findByUserId(String userId);

    Optional<UserCategorySubscription> findByUserIdAndCategoryId(String userId, UUID categoryId);

    boolean existsByUserIdAndCategoryId(String userId, UUID categoryId);

    @Query("SELECT s FROM UserCategorySubscription s WHERE s.userId = :userId AND s.category.id = :categoryId")
    Optional<UserCategorySubscription> findSubscription(@Param("userId") String userId, @Param("categoryId") UUID categoryId);

    void deleteByUserIdAndCategoryId(String userId, UUID categoryId);

    long countByUserId(String userId);

    long countByCategoryId(UUID categoryId);
}