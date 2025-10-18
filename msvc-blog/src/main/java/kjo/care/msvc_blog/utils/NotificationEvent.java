package kjo.care.msvc_blog.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent<T> {
    private String eventId = UUID.randomUUID().toString();
    private String sourceService;
    private String eventType;
    private LocalDateTime timestamp = LocalDateTime.now();
    private T payload;
}
