package kjo.care.msvc_notification.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kjo.care.msvc_notification.dto.ApiResponseDto;
import kjo.care.msvc_notification.dto.NotificationResponseDto;
import kjo.care.msvc_notification.services.NotificationService;
import kjo.care.msvc_notification.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Validated
@Log4j2
@SecurityRequirement(name = "securityToken")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Obtener todas las Notificaciones", description = "Devuelve todos las notificaciones existentes")
    @ApiResponse(responseCode = "200", description = "Notificaciones obtenidas correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron Notificaciones")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<NotificationResponseDto>>> getNotifications(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        log.info("Fetching notifications for user: {}", userId);
        List<NotificationResponseDto> notifications = notificationService.getNotificationsForUser(userId);

        if (notifications.isEmpty()) {
            log.info("No notifications found for user: {}", userId);
            return ResponseBuilder.buildResponse(HttpStatus.OK, "Notficaciones obtenidas correctamente", true, null);
        }

        log.info("Found {} notifications for user: {}", notifications.size(), userId);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Notificaciones obtenidas correctamente", true, notifications);
    }

    @Operation(summary = "Leer una Notificación", description = "Marca una Notificación como leída")
    @ApiResponse(responseCode = "200", description = "Notificacion leída correctamente")
    @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    @PutMapping("/{notificationId}")
    public ResponseEntity<ApiResponseDto<NotificationResponseDto>> readNotification(@PathVariable UUID notificationId) {
        NotificationResponseDto notification = notificationService.readNotification(notificationId);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Notificación leída correctamente", true, notification);
    }

}
