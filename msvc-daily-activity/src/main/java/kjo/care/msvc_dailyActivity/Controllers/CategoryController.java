package kjo.care.msvc_dailyActivity.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kjo.care.msvc_dailyActivity.DTOs.ApiResponseDto;
import kjo.care.msvc_dailyActivity.DTOs.CategoryRequestDTO;
import kjo.care.msvc_dailyActivity.DTOs.CategoryResponseDTO;
import kjo.care.msvc_dailyActivity.Utils.ResponseBuilder;
import kjo.care.msvc_dailyActivity.Services.ICategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
@SecurityRequirement(name = "securityToken")
@Tag(name = "Categorías", description = "API para gestionar categorías de actividades diarias")
public class CategoryController {

    private final ICategoryService categoryService;

    @Operation(
            summary = "Obtener todas las categorías",
            description = "Recupera una lista de todas las categorías disponibles",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CategoryResponseDTO>>> getAllCategories() {
        log.info("GET /categories - Obteniendo todas las categorías");
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();

        if (categories.isEmpty()) {
            log.info("No se encontraron categorías");
            return ResponseBuilder.buildResponse(
                    HttpStatus.OK,
                    "No se encontraron categorías",
                    true,
                    categories
            );
        }

        log.info("Categorías obtenidas correctamente, total: {}", categories.size());
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Categorías obtenidas correctamente",
                true,
                categories
        );
    }

    @Operation(
            summary = "Obtener categoría por ID",
            description = "Recupera una categoría específica por su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
                    @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CategoryResponseDTO>> getCategoryById(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable UUID id) {
        log.info("GET /categories/{} - Obteniendo categoría", id);
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        log.info("Categoría obtenida correctamente con ID: {}", id);
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Categoría obtenida correctamente",
                true,
                category
        );
    }

    @Operation(
            summary = "Crear nueva categoría",
            description = "Crea una nueva categoría",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @PreAuthorize("hasRole('admin_client_role')")
    @PostMapping
    public ResponseEntity<ApiResponseDto<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        log.info("POST /categories - Creando categoría: {}", requestDTO.getName());
        CategoryResponseDTO createdCategory = categoryService.createCategory(requestDTO);
        log.info("Categoría creada exitosamente con ID: {}", createdCategory.getId());
        return ResponseBuilder.buildResponse(
                HttpStatus.CREATED,
                "Categoría creada exitosamente",
                true,
                createdCategory
        );
    }

    @Operation(
            summary = "Actualizar categoría",
            description = "Actualiza una categoría existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @PreAuthorize("hasRole('admin_client_role')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CategoryResponseDTO>> updateCategory(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        log.info("PUT /categories/{} - Actualizando categoría", id);
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, requestDTO);
        log.info("Categoría actualizada exitosamente con ID: {}", id);
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Categoría actualizada exitosamente",
                true,
                updatedCategory
        );
    }

    @Operation(
            summary = "Eliminar categoría",
            description = "Elimina una categoría existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categoría eliminada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
                    @ApiResponse(responseCode = "401", description = "No autorizado")
            }
    )
    @PreAuthorize("hasRole('admin_client_role')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCategory(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable UUID id) {
        log.info("DELETE /categories/{} - Eliminando categoría", id);
        categoryService.deleteCategory(id);
        log.info("Categoría eliminada exitosamente con ID: {}", id);
        return ResponseBuilder.buildResponse(
                HttpStatus.OK,
                "Categoría eliminada exitosamente",
                true,
                null
        );
    }
}