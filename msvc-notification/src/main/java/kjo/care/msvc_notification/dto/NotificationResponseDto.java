package kjo.care.msvc_notification.dto;

import kjo.care.msvc_notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponseDto {
    private UUID id;
    private NotificationType type;
    private String title;
    private String message;
    private String link;
    private boolean isRead;
    private LocalDateTime createdAt;
}
