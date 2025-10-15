package kjo.care.msvc_notification.repositories;

import kjo.care.msvc_notification.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientUserId(String recipientUserId);
    boolean existsBySourceEventId(UUID sourceEventId);
}
