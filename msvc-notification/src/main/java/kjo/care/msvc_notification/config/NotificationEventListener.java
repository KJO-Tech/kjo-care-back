package kjo.care.msvc_notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kjo.care.msvc_notification.dto.CommentEventDto;
import kjo.care.msvc_notification.dto.ReactionEventDto;
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
        log.info("Evento recibido desde {}: tipo {}", event.getSourceService(), event.getEventType());

        switch (event.getEventType()) {
            case "LIKE":
                handleReaction((LinkedHashMap<String, Object>) event.getPayload());
                break;
            case "COMMENT":
                handleComment((LinkedHashMap<String, Object>) event.getPayload());
                break;
            default:
                log.warn("Tipo de evento no reconocido: {}", event.getEventType());
        }
    }

    private void handleReaction(LinkedHashMap<String, Object> payload) {
        try {
            ReactionEventDto reactionEvent = objectMapper.convertValue(payload, ReactionEventDto.class);

            log.info("Procesando reacción: {} de usuario {} en blog {}",
                    reactionEvent.getType(),
                    reactionEvent.getReactorUsername(),
                    reactionEvent.getBlogId());

            if (reactionEvent.getType() == ReactionType.LIKE) {
                notificationService.createLikeNotification(
                        reactionEvent.getBlogAuthorId(),
                        reactionEvent.getReactorUserId(),
                        reactionEvent.getReactorUsername(),
                        reactionEvent.getBlogId(),
                        reactionEvent.getReactionId()
                );
                log.info("Notificación de LIKE creada exitosamente para usuario {}", reactionEvent.getBlogAuthorId());
            }
        } catch (Exception e) {
            log.error("Error al procesar reacción: {}", e.getMessage(), e);
        }
    }

    private void handleComment(LinkedHashMap<String, Object> payload) {
        try {
            CommentEventDto commentEvent = objectMapper.convertValue(payload, CommentEventDto.class);

            log.info("Procesando comentario de usuario {} en blog {}",
                    commentEvent.getCommenterUsername(),
                    commentEvent.getBlogId());

            notificationService.createCommentNotification(
                    commentEvent.getBlogAuthorId(),
                    commentEvent.getCommenterUserId(),
                    commentEvent.getCommenterUsername(),
                    commentEvent.getBlogId(),
                    commentEvent.getCommentId()
            );
            log.info("Notificación de COMMENT creada exitosamente para usuario {}", commentEvent.getBlogAuthorId());
        } catch (Exception e) {
            log.error("Error al procesar comentario: {}", e.getMessage(), e);
        }
    }
}
