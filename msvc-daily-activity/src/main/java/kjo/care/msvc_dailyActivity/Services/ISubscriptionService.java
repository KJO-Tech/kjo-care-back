package kjo.care.msvc_dailyActivity.Services;

import kjo.care.msvc_dailyActivity.DTOs.SubscriptionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ISubscriptionService {

    SubscriptionResponseDTO subscribe(String userId, UUID categoryId);

    void unsubscribe(String userId, UUID categoryId);

    List<SubscriptionResponseDTO> getMySubscriptions(String userId);

    boolean isSubscribed(String userId, UUID categoryId);

    long countSubscriptionsByUser(String userId);
}