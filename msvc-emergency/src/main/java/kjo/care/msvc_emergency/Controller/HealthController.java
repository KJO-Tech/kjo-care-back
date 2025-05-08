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
import kjo.care.msvc_emergency.dto.HealthRequestDto;
import kjo.care.msvc_emergency.dto.HealthResponseDto;
import kjo.care.msvc_emergency.services.HealthService;
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

@RestController
@RequestMapping("/centers")
@RequiredArgsConstructor
@Validated
@Log4j2
@SecurityRequirement(name = "securityToken")
@Tag(name = "Health Center", description = "Operations for Health Center")
public class HealthController {

    private final HealthService healthService;

    @Operation(summary = "Obtener todos los Centros de Salud", description = "Devuelve todos los centros de salud existentes")
    @ApiResponse(responseCode = "200", description = "Centros de Salud obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron los Centros de Salud")
    @PreAuthorize("hasRole('admin_client_role')")
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<HealthResponseDto> response = healthService.findAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener todos los Centros de Salud", description = "Devuelve todos los centros de salud activos")
    @ApiResponse(responseCode = "200", description = "Centros de Salud obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron los Centros de Salud")
    @GetMapping
    public ResponseEntity<?> findAllActive() {
        List<HealthResponseDto> response = healthService.findAllActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener Centro de Salud por ID", description = "Devuelve un centro de salud por su ID")
    @ApiResponse(responseCode = "200", description = "Centro de Salud obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Centro de Salud no encontrado")
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable @Positive(message = "El ID debe ser positivo") Long id) {
        HealthResponseDto response = healthService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear un Centro de Salud", description = "Crea un centro de salud")
    @ApiResponse(responseCode = "201", description = "Centro de Salud creado correctamente")
    @ApiResponse(responseCode = "400", description = "No se pudo crear el centro de Salud")
    @PostMapping
    public ResponseEntity<?> create(@ModelAttribute @Validated HealthRequestDto healthCenter, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        HealthResponseDto createHealth = healthService.save(healthCenter, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createHealth);
    }

    @PutMapping(path = "/{id}")
    @Operation(summary = "Actualizar un Centro de Salud", description = "Actualiza solo los campos proporcionados")
    @ApiResponse(responseCode = "200", description = "Centro de Salud actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Centro de Salud no encontrado")
    public ResponseEntity<?> update(@PathVariable @Positive(message = "El ID debe ser positivo") Long id, @ModelAttribute @Validated HealthRequestDto healthCenter
            , @AuthenticationPrincipal Jwt jwt) {
        String authenticatedUserId = jwt.getSubject();
        HealthResponseDto updated = healthService.update(id, healthCenter,authenticatedUserId);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar una centro de salud por ID", description = "Elimina un centro de salud por su ID")
    @ApiResponse(responseCode = "204", description = "Centro de Salud eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontró el Centro de Salud")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable @Positive(message = "El ID debe ser positivo") Long id,  @AuthenticationPrincipal Jwt jwt) {
        String authenticatedUserId = jwt.getSubject();
        healthService.delete(id, authenticatedUserId);
        return ResponseEntity.noContent().build();
    }
}
