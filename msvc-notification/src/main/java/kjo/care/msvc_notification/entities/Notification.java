package kjo.care.msvc_notification.entities;

import jakarta.persistence.*;
import kjo.care.msvc_notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification", uniqueConstraints = @UniqueConstraint(columnNames = {"sourceEventId", "type", "recipientUserId"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String recipientUserId;

    private String actorUserId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;

    @Column(length = 1000)
    private String message;

    private String link;
    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    private UUID sourceEventId;

    @Column(length = 2000)
    private String metadata;
}
