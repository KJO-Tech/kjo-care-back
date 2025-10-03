package kjo.care.msvc_emergency.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import kjo.care.msvc_emergency.dto.*;
import kjo.care.msvc_emergency.services.HealthService;
import kjo.care.msvc_emergency.utils.ResponseBuilder;
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
    public ResponseEntity<ApiResponseDto<List<HealthResponseDto>>> findAll() {
        List<HealthResponseDto> response = healthService.findAll();
        if (response.isEmpty()){
            return ResponseBuilder.buildResponse(HttpStatus.NO_CONTENT, "No se encontraron los Centros de Salud", true, response);
        }
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Centros de Salud obtenidos correctamente", true, response);
    }

    @Operation(summary = "Obtener todos los Centros de Salud", description = "Devuelve todos los centros de salud activos")
    @ApiResponse(responseCode = "200", description = "Centros de Salud obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron los Centros de Salud")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<HealthResponseDto>>> findAllActive() {
        List<HealthResponseDto> response = healthService.findAllActive();
        if (response.isEmpty()){
            return ResponseBuilder.buildResponse(HttpStatus.NO_CONTENT, "No se encontraron los Centros de Salud", true, response);
        }
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Centros de Salud obtenidos correctamente", true, response);
    }

    @Operation(summary = "Obtener Centro de Salud por ID", description = "Devuelve un centro de salud por su ID")
    @ApiResponse(responseCode = "200", description = "Centro de Salud obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Centro de Salud no encontrado")
    @GetMapping("/getById/{id}")
    public ResponseEntity<ApiResponseDto<HealthResponseDto>> getById(@PathVariable UUID id) {
        HealthResponseDto response = healthService.findById(id);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Centro de Salud obtenido correctamente", true, response);
    }

    @Operation(summary = "Crear un Centro de Salud", description = "Crea un centro de salud")
    @ApiResponse(responseCode = "201", description = "Centro de Salud creado correctamente")
    @ApiResponse(responseCode = "400", description = "No se pudo crear el centro de Salud")
    @PostMapping
    public ResponseEntity<ApiResponseDto<HealthResponseDto>> create(@ModelAttribute @Validated HealthRequestDto healthCenter, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        HealthResponseDto createHealth = healthService.save(healthCenter, userId);
        return ResponseBuilder.buildResponse(HttpStatus.CREATED, "Centro de Salud creado correctamente", true, createHealth);
    }

    @PutMapping(path = "/{id}")
    @Operation(summary = "Actualizar un Centro de Salud", description = "Actualiza solo los campos proporcionados")
    @ApiResponse(responseCode = "200", description = "Centro de Salud actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Centro de Salud no encontrado")
    public ResponseEntity<ApiResponseDto<HealthResponseDto>> update(@PathVariable UUID id, @ModelAttribute @Validated HealthRequestDto healthCenter
            , @AuthenticationPrincipal Jwt jwt) {
        String authenticatedUserId = jwt.getSubject();
        HealthResponseDto updated = healthService.update(id, healthCenter,authenticatedUserId);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Centro de Salud actualizado correctamente", true, updated);
    }

    @Operation(summary = "Eliminar una centro de salud por ID", description = "Elimina un centro de salud por su ID")
    @ApiResponse(responseCode = "204", description = "Centro de Salud eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontró el Centro de Salud")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Object>> delete(@PathVariable UUID id,  @AuthenticationPrincipal Jwt jwt) {
        String authenticatedUserId = jwt.getSubject();
        healthService.delete(id, authenticatedUserId);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Centro de Salud eliminado correctamente", true, null);
    }

    @Operation(summary = "Obtener cantidad total de centros de salud",
            description = "Devuelve el número total de centros de salud registrados")
    @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente")
    @GetMapping("/health-centers/count")
    public ResponseEntity<HealthCenterCountDto> getTotalHealthCenters() {
        log.info("Petición para obtener cantidad total de centros de salud");
        int count = healthService.countTotalHealthCenters();
        log.info("Total de centros de salud: {}", count);
        return ResponseEntity.ok(new HealthCenterCountDto(count));
    }

    @Operation(summary = "Obtener cantidad de centros de salud activos",
            description = "Devuelve el número total de centros de salud activos")
    @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente")
    @GetMapping("/health-centers/count/active")
    public ResponseEntity<HealthCenterCountDto> getActiveHealthCenters() {
        log.info("Petición para obtener cantidad de centros de salud activos");
        int count = healthService.countActiveHealthCenters();
        log.info("Total de centros de salud activos: {}", count);
        return ResponseEntity.ok(new HealthCenterCountDto(count));
    }

    @Operation(summary = "Obtener cantidad de centros de salud del mes anterior",
            description = "Devuelve el número de centros de salud registrados en el mes anterior")
    @ApiResponse(responseCode = "200", description = "Conteo del mes anterior obtenido correctamente")
    @GetMapping("/health-centers/count/previous-month")
    public ResponseEntity<HealthCenterCountDto> getPreviousMonthHealthCenters() {
        log.info("Petición para obtener cantidad de centros de salud del mes anterior");
        int count = healthService.countPreviousMonthHealthCenters();
        log.info("Total de centros de salud del mes anterior: {}", count);
        return ResponseEntity.ok(new HealthCenterCountDto(count));
    }

    @Operation(summary = "Obtener Centros de Salud cercanos",
            description = "Devuelve los centros de salud dentro de un radio en km desde la ubicación del usuario")
    @ApiResponse(responseCode = "200", description = "Centros de Salud cercanos obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron Centros de Salud cercanos")
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponseDto<List<HealthResponseDto>>> getNearbyHealthCenters(
            @Parameter(description = "Latitud del usuario", required = true)
            @RequestParam double lat,
            @Parameter(description = "Longitud del usuario", required = true)
            @RequestParam double lon,
            @Parameter(description = "Distancia de búsqueda en km", required = true, example = "10")
            @RequestParam @Positive double distanceKm) {

        List<HealthResponseDto> response = healthService.findNearby(lat, lon, distanceKm);

        if (response.isEmpty()) {
            return ResponseBuilder.buildResponse(
                    HttpStatus.NO_CONTENT,
                    "No se encontraron Centros de Salud cercanos",
                    true,
                    response
            );
        }

        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Centros de Salud cercanos obtenidos correctamente",
                true,
                response
        );
    }
}
