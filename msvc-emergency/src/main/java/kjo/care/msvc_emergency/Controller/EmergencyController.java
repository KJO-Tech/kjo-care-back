package kjo.care.msvc_emergency.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import kjo.care.msvc_emergency.dto.EmergencyRequestDto;
import kjo.care.msvc_emergency.dto.EmergencyResponseDto;
import kjo.care.msvc_emergency.dto.StatsResponseDto;
import kjo.care.msvc_emergency.services.EmergencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Validated
@Log4j2
@SecurityRequirement(name = "securityToken")
@Tag(name = "Emergency Resource", description = "Operations for Emergency Resource")
public class EmergencyController {

    private final EmergencyService emergencyService;

    @Operation(summary = "Obtener todos los Recursos de Emergencia", description = "Devuelve todos los recursos de emergencia existentes")
    @ApiResponse(responseCode = "200", description = "Recursos de Emergencia obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron los Recursos de Emergencia")
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<EmergencyResponseDto> response = emergencyService.findAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener todos los Recursos de Emergencia activos", description = "Devuelve todos los recursos de emergencia activos")
    @ApiResponse(responseCode = "200", description = "Recursos de Emergencia obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron los Recursos de Emergencia")
    @GetMapping
    public ResponseEntity<?> findAllActive() {
        List<EmergencyResponseDto> response = emergencyService.findAllActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener Recurso de emergencia por ID", description = "Devuelve un recurso de emergencia por su ID")
    @ApiResponse(responseCode = "200", description = "Recurso de Emergencia obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Recurso de Emergencia no encontrado")
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        EmergencyResponseDto response = emergencyService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener estadísticas", description = "Devuelve las estadísticas de Emergencia")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron estadísticas")
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        StatsResponseDto response = emergencyService.setStats();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear un Recurso de Emergencia", description = "Crea un recurso de emergencia")
    @ApiResponse(responseCode = "201", description = "Recurso de Emergencia creado correctamente")
    @ApiResponse(responseCode = "400", description = "No se pudo crear el recurso de Emergencia")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@Parameter(description = "Datos del blog", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = EmergencyRequestDto.class)))
                                    @ModelAttribute @Validated EmergencyRequestDto emergency,
                                    @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        EmergencyResponseDto createEmergency = emergencyService.save(emergency, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createEmergency);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar un Recurso de Emergencia", description = "Actualiza solo los campos proporcionados")
    @ApiResponse(responseCode = "200", description = "Recurso de Emergencia actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Recurso de Emergencia no encontrado")
    public ResponseEntity<?> update(@PathVariable UUID id
            , @Parameter(description = "Datos del blog", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = EmergencyRequestDto.class)))
                                    @ModelAttribute @Validated EmergencyRequestDto emergency
            , @AuthenticationPrincipal Jwt jwt) {
        String authenticatedUserId = jwt.getSubject();
        EmergencyResponseDto updated = emergencyService.update(id, emergency,authenticatedUserId);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar una recurso de emergencia por ID", description = "Elimina un recurso de emergencia por su ID")
    @ApiResponse(responseCode = "204", description = "Recurso de Emergencia eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontró el Recurso de Emergencia")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id,  @AuthenticationPrincipal Jwt jwt) {
        String authenticatedUserId = jwt.getSubject();
        emergencyService.delete(id, authenticatedUserId);
        return ResponseEntity.noContent().build();
    }

}
