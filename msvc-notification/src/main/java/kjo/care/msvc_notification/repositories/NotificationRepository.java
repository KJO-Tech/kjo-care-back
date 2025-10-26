package kjo.care.msvc_notification.repositories;

import kjo.care.msvc_notification.entities.Notification;
import kjo.care.msvc_notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    @Query("SELECT n FROM Notification n WHERE n.recipientUserId = :recipientUserId ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(@Param("recipientUserId") String recipientUserId);
    boolean existsBySourceEventId(UUID sourceEventId);
    boolean existsBySourceEventIdAndTypeAndRecipientUserId(UUID sourceEventId, NotificationType type, String recipientUserId);
}
