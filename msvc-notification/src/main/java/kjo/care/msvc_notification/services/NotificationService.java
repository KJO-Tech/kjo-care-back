package kjo.care.msvc_notification.services;

import kjo.care.msvc_notification.dto.NotificationResponseDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void createLikeNotification(String recipientUserId, String actorUserId, String actorUsername, UUID blogId, UUID reactionId);
    List<NotificationResponseDto> getNotificationsForUser(String userId);
}
