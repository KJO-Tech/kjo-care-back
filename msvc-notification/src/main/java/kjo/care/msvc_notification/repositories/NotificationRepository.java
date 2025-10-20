package kjo.care.msvc_notification.repositories;

import kjo.care.msvc_notification.entities.Notification;
import kjo.care.msvc_notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientUserId(String recipientUserId);
    boolean existsBySourceEventId(UUID sourceEventId);
    boolean existsBySourceEventIdAndTypeAndRecipientUserId(UUID sourceEventId, NotificationType type, String recipientUserId);
}
