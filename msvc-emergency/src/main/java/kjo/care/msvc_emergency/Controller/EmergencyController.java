package kjo.care.msvc_emergency.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import kjo.care.msvc_emergency.dto.ApiResponseDto;
import kjo.care.msvc_emergency.dto.EmergencyRequestDto;
import kjo.care.msvc_emergency.dto.EmergencyResponseDto;
import kjo.care.msvc_emergency.dto.StatsResponseDto;
import kjo.care.msvc_emergency.services.EmergencyService;
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
    public ResponseEntity<ApiResponseDto<List<EmergencyResponseDto>>> findAll() {
        List<EmergencyResponseDto> response = emergencyService.findAll();
        if (response.isEmpty()){
            return ResponseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron los Recursos de Emergencia", true, response);
        }
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Recursos de Emergencia obtenidos correctamente", true, response);
    }

    @Operation(summary = "Obtener todos los Recursos de Emergencia activos", description = "Devuelve todos los recursos de emergencia activos")
    @ApiResponse(responseCode = "200", description = "Recursos de Emergencia obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron los Recursos de Emergencia")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<EmergencyResponseDto>>> findAllActive() {
        List<EmergencyResponseDto> response = emergencyService.findAllActive();
        if (response.isEmpty()){
            return ResponseBuilder.buildResponse(HttpStatus.NOT_FOUND, "No se encontraron los Recursos de Emergencia", true, response);
        }
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Recursos de Emergencia obtenidos correctamente", true, response);
    }

    @Operation(summary = "Obtener Recurso de emergencia por ID", description = "Devuelve un recurso de emergencia por su ID")
    @ApiResponse(responseCode = "200", description = "Recurso de Emergencia obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Recurso de Emergencia no encontrado")
    @GetMapping("/getById/{id}")
    public ResponseEntity<ApiResponseDto<EmergencyResponseDto>> getById(@PathVariable UUID id) {
        EmergencyResponseDto response = emergencyService.findById(id);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Recurso de Emergencia obtenido correctamente", true, response);
    }

    @Operation(summary = "Obtener estadísticas", description = "Devuelve las estadísticas de Emergencia")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron estadísticas")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDto<StatsResponseDto>> getStats() {
        StatsResponseDto response = emergencyService.setStats();
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Estadísticas obtenidas correctamente", true, response);
    }

    @Operation(summary = "Crear un Recurso de Emergencia", description = "Crea un recurso de emergencia")
    @ApiResponse(responseCode = "201", description = "Recurso de Emergencia creado correctamente")
    @ApiResponse(responseCode = "400", description = "No se pudo crear el recurso de Emergencia")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<EmergencyResponseDto>> create(@Parameter(description = "Datos del blog", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = EmergencyRequestDto.class)))
                                    @ModelAttribute @Validated EmergencyRequestDto emergency,
                                    @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        EmergencyResponseDto createEmergency = emergencyService.save(emergency, userId);
        return ResponseBuilder.buildResponse(HttpStatus.CREATED, "Recurso de Emergencia creado correctamente", true, createEmergency);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar un Recurso de Emergencia", description = "Actualiza solo los campos proporcionados")
    @ApiResponse(responseCode = "200", description = "Recurso de Emergencia actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Recurso de Emergencia no encontrado")
    public ResponseEntity<ApiResponseDto<EmergencyResponseDto>> update(@PathVariable UUID id
            , @Parameter(description = "Datos del blog", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = EmergencyRequestDto.class)))
                                    @ModelAttribute @Validated EmergencyRequestDto emergency
            , @AuthenticationPrincipal Jwt jwt) {
        String authenticatedUserId = jwt.getSubject();
        EmergencyResponseDto updated = emergencyService.update(id, emergency,authenticatedUserId);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Recurso de Emergencia actualizado correctamente", true, updated);
    }

    @Operation(summary = "Eliminar una recurso de emergencia por ID", description = "Elimina un recurso de emergencia por su ID")
    @ApiResponse(responseCode = "204", description = "Recurso de Emergencia eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontró el Recurso de Emergencia")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Object>> delete(@PathVariable UUID id,  @AuthenticationPrincipal Jwt jwt) {
        String authenticatedUserId = jwt.getSubject();
        emergencyService.delete(id, authenticatedUserId);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Recurso de Emergencia eliminado correctamente", true, null);
    }

}
