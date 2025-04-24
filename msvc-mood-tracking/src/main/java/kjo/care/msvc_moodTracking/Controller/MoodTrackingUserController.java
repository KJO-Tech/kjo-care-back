package kjo.care.msvc_moodTracking.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.MoodUserRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.UserDTO;
import kjo.care.msvc_moodTracking.DTOs.MoodUserDTOs.UserMoodDTO;
import kjo.care.msvc_moodTracking.Entities.MoodUser;
import kjo.care.msvc_moodTracking.Repositories.MoodUserRepository;
import kjo.care.msvc_moodTracking.services.MoodUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user-mood")
@RequiredArgsConstructor
@Validated
@Log4j2
@Tag(name = "Usuario Mood", description = "API para gestionar estados de ánimo de usuarios")
@SecurityRequirement(name = "securityToken")
public class MoodTrackingUserController {
    private final MoodUserService moodUserService;
    private final MoodUserRepository moodUserRepository;
    private final ModelMapper modelMapper;

    @Operation(summary = "Obtener todos los usuarios con sus estados de ánimo",
            description = "Retorna todos los usuarios junto con sus registros de estado de ánimo")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios con sus estados de ánimo")
    @GetMapping("")
    public ResponseEntity<List<UserMoodDTO>> getAllUsersWithMoods() {
        log.info("Solicitud para obtener todos los usuarios con sus estados de ánimo");
        List<UserMoodDTO> usersWithMoods = moodUserService.getAllUsersWithMoods();

        log.info("Retornando {} registros de estados de ánimo", usersWithMoods.size());
        return ResponseEntity.ok(usersWithMoods);
    }


    @Operation(summary = "Obtener estados de ánimo del usuario autenticado",
            description = "Devuelve todos los estados de ánimo registrados del usuario actual")
    @ApiResponse(responseCode = "200", description = "Estados de ánimo obtenidos correctamente")
    @GetMapping("/my-moods")
    public Mono<ResponseEntity<List<UserMoodDTO>>> getCurrentUserMoods(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        log.info("Petición para obtener estados de ánimo del usuario: {}", userId);

        return moodUserService.getCurrentUserMoods(userId)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(List.of()));
    }


    @Operation(summary = "Registrar estado de ánimo del usuario",
            description = "Registra el estado de ánimo seleccionado por el usuario actual")
    @ApiResponse(responseCode = "201", description = "Estado de ánimo registrado correctamente")
    @ApiResponse(responseCode = "404", description = "Estado de ánimo no encontrado")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @PostMapping("/track-mood")
    public ResponseEntity<UserMoodDTO> trackUserMood(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid MoodUserRequestDto moodUserRequestDto) {

        String userId = jwt.getSubject();
        log.info("Petición para registrar estado de ánimo del usuario: {}, mood id: {}",
                userId, moodUserRequestDto.moodId());

        UserMoodDTO result = moodUserService.trackUserMood(userId, moodUserRequestDto);

        log.info("Estado de ánimo registrado correctamente para el usuario: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}