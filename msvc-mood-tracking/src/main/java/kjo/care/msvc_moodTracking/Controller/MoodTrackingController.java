package kjo.care.msvc_moodTracking.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodPageResponseDto;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodRequestDto;
import kjo.care.msvc_moodTracking.DTOs.MoodDTOs.MoodResponseDto;
import kjo.care.msvc_moodTracking.services.MoodService;
import kjo.care.msvc_moodTracking.services.MoodUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Validated
@Log4j2
@Tag(name = "Mood Tracking", description = "MoodTracking API")
public class MoodTrackingController {
    private final MoodUserService moodUserService;
    private final MoodService moodService;

    @Operation(summary = "Obtener todos los estados de animo", description = "Devuelve todos los estado de animos existentes")
    @ApiResponse(responseCode = "200", description = "Estados de animo obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron estados de animo")
    @GetMapping("")
    public ResponseEntity<MoodPageResponseDto> findAll(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "La pagina tiene que ser 0 o mayor") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "El tamaño debe ser al menos 1") int size) {
        log.info("Peticion findAll obtenida , page {},size {}", page, size);
        MoodPageResponseDto response = moodService.findAllMoods(page, size);
        log.info("Se retornaron {}", response.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener estado de animo por ID", description = "Devuelve un estado de animo por id")
    @ApiResponse(responseCode = "200", description = "Estado de animo obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Estado de animo no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<MoodResponseDto> getById(@PathVariable @Positive(message = "El ID debe ser positivo") Long id) {
        log.info("Peticion getById para obtener estado de animo con id {}", id);
        MoodResponseDto response = moodService.findMoodById(id);
        log.info("Mood obtenido : id = {}", id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear un estado de animo", description = "Crea un estado de animo")
    @ApiResponse(responseCode = "201", description = "Estado de animo creado correctamente")
    @ApiResponse(responseCode = "400", description = "No se pudo crear el estado de animo")
    @PostMapping("")
    public ResponseEntity<MoodResponseDto> create(@RequestBody @Validated MoodRequestDto mood) {
        log.info("Peticion recibida para crear un mood");
        MoodResponseDto createMood = moodService.saveMood(mood);
        log.info("Mood creado : id={}", createMood.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createMood);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente un estado de ánimo", description = "Actualiza solo los campos proporcionados")
    @ApiResponse(responseCode = "200", description = "Estado de ánimo actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Estado de ánimo no encontrado")
    public ResponseEntity<MoodResponseDto> update(@PathVariable @Positive(message = "El ID debe ser positivo") Long id, @RequestBody MoodRequestDto mood) {
        log.info("Petición recibida para actualizar parcialmente un mood con id {}", id);
        MoodResponseDto updatedMood = moodService.updateMood(id, mood);
        log.info("Mood actualizado parcialmente: id={}", updatedMood.getId());
        return ResponseEntity.ok(updatedMood);
    }

    @Operation(summary = "Eliminar un estado de animo por ID", description = "Elimina un estado de animo por ID")
    @ApiResponse(responseCode = "204", description = "Estado de animo eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontró el estado de animo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "El ID debe ser positivo") Long id) {
        log.info("Peticion para eliminar un mood con id {}", id);
        moodService.deleteMood(id);
        log.info("Mood eliminado : id {}", id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Activar o desactivar estado de ánimo",
            description = "Cambia el estado de activo a inactivo o viceversa")
    @ApiResponse(responseCode = "200", description = "Estado de ánimo actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Estado de ánimo no encontrado")
    public ResponseEntity<MoodResponseDto> toggleStatus(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id) {
        log.info("Petición recibida para cambiar el estado activo/inactivo del mood con id {}", id);
        MoodResponseDto updatedMood = moodService.toggleMoodStatus(id);
        log.info("Mood con id {} cambiado a estado activo: {}", id, updatedMood.getIsActive());
        return ResponseEntity.ok(updatedMood);
    }
}
