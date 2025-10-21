package kjo.care.msvc_notification.services.Impl;

import kjo.care.msvc_notification.dto.*;
import kjo.care.msvc_notification.entities.Notification;
import kjo.care.msvc_notification.enums.NotificationType;
import kjo.care.msvc_notification.mappers.NotificationMapper;
import kjo.care.msvc_notification.repositories.NotificationRepository;
import kjo.care.msvc_notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@Validated
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void createLikeNotification(ReactionEventDto event) {
        if (notificationRepository.existsBySourceEventIdAndTypeAndRecipientUserId(event.getReactionId(), NotificationType.LIKE, event.getBlogAuthorId())) {
            log.warn("Notificación LIKE ya existe para el usuario {} y reactionId: {}. Saltando.", event.getBlogAuthorId(), event.getReactionId());
            return;
        }

        if (event.getBlogAuthorId().equals(event.getReactorUserId())) {
            log.info("Usuario reaccionó a su propia publicación. No se crea notificación.");
            return;
        }

        Notification notification = Notification.builder()
                .recipientUserId(event.getBlogAuthorId())
                .actorUserId(event.getReactorUserId())
                .type(NotificationType.LIKE)
                .title("Tu publicación recibió un like")
                .message(event.getReactorUsername() + " le dio like a tu publicación.")
                .link("/blogs/" + event.getBlogId().toString())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .sourceEventId(event.getReactionId())
                .metadata(String.format("{\"topic\":\"blog-reactions\",\"blogId\":\"%s\"}", event.getBlogId()))
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notificación LIKE creada para usuario {}.", event.getBlogAuthorId());

        String userId = savedNotification.getRecipientUserId();
        String destination = "/queue/notifications";
        messagingTemplate.convertAndSendToUser(
            userId,
            destination,
            notificationMapper.toDto(savedNotification)
        );
        log.info("Notificación LIKE enviada por WebSocket al usuario {}.", userId);
    }

    @Override
    @Transactional
    public void createCommentNotification(CommentEventDto event) {
        if (notificationRepository.existsBySourceEventIdAndTypeAndRecipientUserId(event.getCommentId(), NotificationType.COMMENT, event.getBlogAuthorId())) {
            log.warn("Notificación COMMENT ya existe para el usuario {} y commentId: {}. Saltando.", event.getBlogAuthorId(), event.getCommentId());
            return;
        }

        if (event.getBlogAuthorId().equals(event.getCommenterUserId())) {
            log.info("Usuario comentó en su propia publicación. No se crea notificación.");
            return;
        }

        Notification notification = Notification.builder()
                .recipientUserId(event.getBlogAuthorId())
                .actorUserId(event.getCommenterUserId())
                .type(NotificationType.COMMENT)
                .title("Tu publicación recibió un nuevo comentario")
                .message(event.getCommenterUsername() + " comentó en tu publicación.")
                .link("/blogs/" + event.getBlogId().toString())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .sourceEventId(event.getCommentId())
                .metadata(String.format("{\"topic\":\"blog-comments\",\"blogId\":\"%s\"}", event.getBlogId()))
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notificación COMMENT creada para usuario {}.", event.getBlogAuthorId());

        String userId = savedNotification.getRecipientUserId();
        String destination = "/queue/notifications";
        messagingTemplate.convertAndSendToUser(
            userId,
            destination,
            notificationMapper.toDto(savedNotification)
        );
        log.info("Notificación COMMENT enviada por WebSocket al usuario {}.", userId);
    }

    @Override
    @Transactional
    public void createCommentReplyNotification(CommentEventDto event) {
        if (notificationRepository.existsBySourceEventIdAndTypeAndRecipientUserId(event.getCommentId(), NotificationType.COMMENT, event.getParentCommentAuthorId())) {
            log.warn("Notificación de respuesta a comentario ya existe para el usuario {} y commentId: {}. Saltando.", event.getParentCommentAuthorId(), event.getCommentId());
            return;
        }

        if (event.getParentCommentAuthorId().equals(event.getCommenterUserId())) {
            log.info("Usuario respondió a su propio comentario. No se crea notificación.");
            return;
        }

        Notification notification = Notification.builder()
                .recipientUserId(event.getParentCommentAuthorId())
                .actorUserId(event.getCommenterUserId())
                .type(NotificationType.COMMENT)
                .title("Alguien respondió a tu comentario")
                .message(event.getCommenterUsername() + " respondió a tu comentario en una publicación.")
                .link("/blogs/" + event.getBlogId().toString())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .sourceEventId(event.getCommentId())
                .metadata(String.format("{\"topic\":\"blog-comments\",\"blogId\":\"%s\"}", event.getBlogId()))
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notificación de respuesta a comentario creada para usuario {}.", event.getParentCommentAuthorId());

        String userId = savedNotification.getRecipientUserId();
        String destination = "/queue/notifications";
        messagingTemplate.convertAndSendToUser(
            userId,
            destination,
            notificationMapper.toDto(savedNotification)
        );
        log.info("Notificación de respuesta a comentario enviada por WebSocket al usuario {}.", userId);
    }

    @Override
    @Transactional
    public void createNewBlogNotification(NewBlogEventDto event) {
        if (notificationRepository.existsBySourceEventIdAndTypeAndRecipientUserId(event.getBlogId(), NotificationType.NEW_BLOG, event.getRecipientId())) {
            log.warn("Notificación NEW_BLOG ya existe para el admin {} y blogId: {}. Saltando.", event.getRecipientId(), event.getBlogId());
            return;
        }

        Notification notification = Notification.builder()
                .recipientUserId(event.getRecipientId())
                .actorUserId(event.getAuthorId())
                .type(NotificationType.NEW_BLOG)
                .title("Nuevo blog para revisión")
                .message(String.format("El usuario '%s' ha publicado un nuevo blog: '%s'", event.getAuthorUsername(), event.getBlogTitle()))
                .link("/blogs/" + event.getBlogId().toString())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .sourceEventId(event.getBlogId())
                .metadata(String.format("{\"topic\":\"new-blogs\",\"blogId\":\"%s\"}", event.getBlogId()))
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notificación de NUEVO BLOG guardada para el administrador {}.", event.getRecipientId());

        String userId = savedNotification.getRecipientUserId();
        String destination = "/queue/notifications-" + userId;

        messagingTemplate.convertAndSend(
                destination,
                notificationMapper.toDto(savedNotification)
        );

        log.info("Notificación de NUEVO BLOG enviada por WebSocket al destino {}.", destination);
    }

    @Override
    @Transactional
    public void createBlogRejectedNotification(BlogRejectedEventDto event) {
        if (notificationRepository.existsBySourceEventIdAndTypeAndRecipientUserId(event.getBlogId(), NotificationType.BLOG_REJECTED, event.getAuthorId())) {
            log.warn("Notificación BLOG_REJECTED ya existe para el usuario {} y blogId: {}. Saltando.", event.getAuthorId(), event.getBlogId());
            return;
        }

        Notification notification = Notification.builder()
                .recipientUserId(event.getAuthorId())
                .actorUserId(null)
                .type(NotificationType.BLOG_REJECTED)
                .title("Tu publicación ha sido rechazada")
                .message(String.format("Tu blog titulado '%s' no cumple con nuestras políticas y ha sido rechazado.", event.getBlogTitle()))
                .link(null)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .sourceEventId(event.getBlogId())
                .metadata(String.format("{\"topic\":\"blog-rejected\",\"blogId\":\"%s\"}", event.getBlogId()))
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notificación de BLOG RECHAZADO guardada para el usuario {}.", event.getAuthorId());

        String userId = savedNotification.getRecipientUserId();
        String destination = "/queue/notifications";
        messagingTemplate.convertAndSendToUser(
            userId,
            destination,
            notificationMapper.toDto(savedNotification)
        );
        log.info("Notificación de BLOG RECHAZADO enviada por WebSocket al usuario {}.", userId);
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
