package kjo.care.msvc_notification.services.Impl;

import kjo.care.msvc_notification.dto.NotificationResponseDto;
import kjo.care.msvc_notification.entities.Notification;
import kjo.care.msvc_notification.enums.NotificationType;
import kjo.care.msvc_notification.mappers.NotificationMapper;
import kjo.care.msvc_notification.repositories.NotificationRepository;
import kjo.care.msvc_notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@Validated
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void createLikeNotification(String recipientUserId, String actorUserId, String actorUsername, UUID blogId, UUID reactionId) {

        if (notificationRepository.existsBySourceEventId(reactionId)) {
            log.info("Notificación ya existe para reactionId: {}", reactionId);
            return;
        }

        if (recipientUserId.equals(actorUserId)) {
            log.info("El usuario {} reaccionó a su propia publicación, no se crea notificación", actorUserId);
            return;
        }

        Notification notification = Notification.builder()
                .recipientUserId(recipientUserId)
                .actorUserId(actorUserId)
                .type(NotificationType.LIKE)
                .title("Tu publicación recibió un like")
                .message(actorUsername + " le dio like a tu publicación.")
                .link("/blogs/" + blogId.toString())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .sourceEventId(reactionId)
                .metadata(String.format("{\"topic\":\"blog-reactions\",\"blogId\":\"%s\"}", blogId))
                .build();

        notificationRepository.save(notification);
        log.info("Notificación LIKE creada para usuario {} por {}", recipientUserId, actorUsername);
    }


    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsForUser(String userId) {
        return notificationRepository.findByRecipientUserId(userId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

}
