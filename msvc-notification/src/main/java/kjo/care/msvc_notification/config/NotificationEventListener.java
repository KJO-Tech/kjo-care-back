package kjo.care.msvc_notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kjo.care.msvc_notification.dto.*;
import kjo.care.msvc_notification.enums.ReactionType;
import kjo.care.msvc_notification.services.NotificationService;
import kjo.care.msvc_notification.utils.NotificationEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Log4j2
@Component
@AllArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "notifications", groupId = "notification-group",
            containerFactory = "validMessageContainerFactory")
    public void consume(NotificationEvent<?> event) {
        if (event == null || event.getEventType() == null) {
            log.error("Evento de Kafka nulo o sin tipo. El mensaje podría estar corrupto.");
            return;
        }

        log.info("Evento [{}] recibido. Procesando...", event.getEventType());

        try {
            switch (event.getEventType()) {
                case "LIKE":
                    handleReaction((LinkedHashMap<String, Object>) event.getPayload());
                    break;
                case "COMMENT":
                    handleComment((LinkedHashMap<String, Object>) event.getPayload());
                    break;
                case "NEW_BLOG":
                    handleNewBlog((LinkedHashMap<String, Object>) event.getPayload());
                    break;
                case "BLOG_REJECTED":
                    handleBlogRejected((LinkedHashMap<String, Object>) event.getPayload());
                    break;
                default:
                    log.warn("Tipo de evento no reconocido: [{}].", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error fatal al procesar el evento de tipo [{}]. Causa: {}", event.getEventType(), e.getMessage(), e);
        }
    }

    private void handleReaction(LinkedHashMap<String, Object> payload) {
        ReactionEventDto reactionEvent = objectMapper.convertValue(payload, ReactionEventDto.class);
        if (reactionEvent.getType() == ReactionType.LIKE) {
            notificationService.createLikeNotification(reactionEvent);
        }
    }

    private void handleComment(LinkedHashMap<String, Object> payload) {
        CommentEventDto commentEvent = objectMapper.convertValue(payload, CommentEventDto.class);

        notificationService.createCommentNotification(commentEvent);

        if (commentEvent.getParentCommentAuthorId() != null) {
            notificationService.createCommentReplyNotification(commentEvent);
        }
    }

    private void handleNewBlog(LinkedHashMap<String, Object> payload) {
        NewBlogEventDto newBlogEvent = objectMapper.convertValue(payload, NewBlogEventDto.class);
        notificationService.createNewBlogNotification(newBlogEvent);
    }

    private void handleBlogRejected(LinkedHashMap<String, Object> payload) {
        BlogRejectedEventDto rejectedEvent = objectMapper.convertValue(payload, BlogRejectedEventDto.class);
        notificationService.createBlogRejectedNotification(rejectedEvent);
    }
}
