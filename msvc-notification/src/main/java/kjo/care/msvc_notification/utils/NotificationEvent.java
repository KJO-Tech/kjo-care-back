package kjo.care.msvc_notification.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent<T> {
    private String eventId;
    private String sourceService;
    private String eventType;
    private LocalDateTime timestamp;
    private T payload;
}
