package kjo.care.msvc_dailyActivity.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kjo.care.msvc_dailyActivity.DTOs.ApiResponseDto;
import kjo.care.msvc_dailyActivity.DTOs.SubscriptionRequestDTO;
import kjo.care.msvc_dailyActivity.DTOs.SubscriptionResponseDTO;
import kjo.care.msvc_dailyActivity.Services.ISubscriptionService;
import kjo.care.msvc_dailyActivity.Utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Validated
@Slf4j
@SecurityRequirement(name = "securityToken")
@Tag(name = "Suscripciones", description = "API para gestionar suscripciones de usuarios a categorías")
public class SubscriptionController {

    private final ISubscriptionService subscriptionService;

    @Operation(
            summary = "Suscribirse a una categoría",
            description = "Permite al usuario suscribirse a una categoría de actividades",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Suscripción exitosa"),
                    @ApiResponse(responseCode = "400", description = "Ya está suscrito o categoría inválida"),
                    @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponseDto<SubscriptionResponseDTO>> subscribe(
            @Valid @RequestBody SubscriptionRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("POST /subscriptions - Usuario {} suscribiéndose a categoría {}", userId, requestDTO.getCategoryId());

        SubscriptionResponseDTO response = subscriptionService.subscribe(userId, requestDTO.getCategoryId());

        log.info("Usuario {} suscrito exitosamente", userId);
        return ResponseBuilder.buildResponse(
                HttpStatus.CREATED,
                "Suscripción exitosa a la categoría",
                true,
                response
        );
    }

    @Operation(
            summary = "Desuscribirse de una categoría",
            description = "Permite al usuario desuscribirse de una categoría",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Desuscripción exitosa"),
                    @ApiResponse(responseCode = "400", description = "No está suscrito a esta categoría"),
                    @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseDto<Void>> unsubscribe(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable UUID categoryId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("DELETE /subscriptions/{} - Usuario {} desuscribiéndose", categoryId, userId);

        subscriptionService.unsubscribe(userId, categoryId);

        log.info("Usuario {} desuscrito exitosamente de categoría {}", userId, categoryId);
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Desuscripción exitosa de la categoría",
                true,
                null
        );
    }

    @Operation(
            summary = "Obtener mis suscripciones",
            description = "Obtiene todas las categorías a las que el usuario está suscrito",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Suscripciones obtenidas exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/my-subscriptions")
    public ResponseEntity<ApiResponseDto<List<SubscriptionResponseDTO>>> getMySubscriptions(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("GET /subscriptions/my-subscriptions - Obteniendo suscripciones de usuario {}", userId);

        List<SubscriptionResponseDTO> subscriptions = subscriptionService.getMySubscriptions(userId);

        if (subscriptions.isEmpty()) {
            log.info("Usuario {} no tiene suscripciones", userId);
            return ResponseBuilder.buildResponse(
                    HttpStatus.OK,
                    "No tienes suscripciones activas",
                    true,
                    subscriptions
            );
        }

        log.info("Usuario {} tiene {} suscripciones", userId, subscriptions.size());
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Suscripciones obtenidas correctamente",
                true,
                subscriptions
        );
    }

    @Operation(
            summary = "Verificar suscripción",
            description = "Verifica si el usuario está suscrito a una categoría específica",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Verificación exitosa"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/check/{categoryId}")
    public ResponseEntity<ApiResponseDto<Boolean>> checkSubscription(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable UUID categoryId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("GET /subscriptions/check/{} - Usuario {}", categoryId, userId);

        boolean isSubscribed = subscriptionService.isSubscribed(userId, categoryId);

        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                isSubscribed ? "Estás suscrito a esta categoría" : "No estás suscrito a esta categoría",
                true,
                isSubscribed
        );
    }

    @Operation(
            summary = "Contar mis suscripciones",
            description = "Obtiene el número total de suscripciones del usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conteo exitoso"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/count")
    public ResponseEntity<ApiResponseDto<Long>> countMySubscriptions(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("GET /subscriptions/count - Usuario {}", userId);

        long count = subscriptionService.countSubscriptionsByUser(userId);

        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Total de suscripciones: " + count,
                true,
                count
        );
    }
}