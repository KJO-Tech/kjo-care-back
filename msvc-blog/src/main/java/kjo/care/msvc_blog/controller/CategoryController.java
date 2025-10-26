package kjo.care.msvc_blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import kjo.care.msvc_blog.dto.ApiResponseDto;
import kjo.care.msvc_blog.dto.CategoryRequestDto;
import kjo.care.msvc_blog.dto.CategoryResponseDto;
import kjo.care.msvc_blog.services.CategoryService;
import kjo.care.msvc_blog.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Validated
@Log4j2
@SecurityRequirement(name = "securityToken")
@Tag(name = "Category", description = "Operations for Category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/hello")
    public String getBlog() {
        return "Hello Blog";
    }

    @Operation(summary = "Obtener todos las categorias", description = "Devuelve todos las categorias existentes")
    @ApiResponse(responseCode = "200", description = "Categorias obtenidas correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron categorias")
    @GetMapping("")
    public ResponseEntity<ApiResponseDto<List<CategoryResponseDto>>> findAll() {
        List<CategoryResponseDto> response = categoryService.findAllCategories();
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Categorias obtenidas correctamente", true, response);
    }

    @Operation(summary = "Obtener Categoria por ID", description = "Devuelve una categoria por su ID")
    @ApiResponse(responseCode = "200", description = "Categoria obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Categoria no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CategoryResponseDto>> getById(@PathVariable UUID id) {
        CategoryResponseDto response = categoryService.findCategoryById(id);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Categoria obtenida correctamente", true, response);
    }

    @Operation(summary = "Crear una Categoria", description = "Crea una categoria")
    @ApiResponse(responseCode = "201", description = "Categoria creada correctamente")
    @ApiResponse(responseCode = "400", description = "No se pudo crear la categoria")
    @PostMapping("")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<ApiResponseDto<CategoryResponseDto>> create(@RequestBody @Validated CategoryRequestDto category) {
        CategoryResponseDto createCategory = categoryService.saveCategory(category);
        return ResponseBuilder.buildResponse(HttpStatus.CREATED, "Categoria creada correctamente", true, createCategory);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar una categoria", description = "Actualiza solo los campos proporcionados")
    @ApiResponse(responseCode = "200", description = "Categoria actualizada correctamente")
    @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<ApiResponseDto<CategoryResponseDto>> update(@PathVariable UUID id, @RequestBody CategoryRequestDto category) {
        CategoryResponseDto updated = categoryService.updateCategory(id, category);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Categoria actualizada correctamente", true, updated);
    }

    @Operation(summary = "Eliminar una categoria por ID", description = "Elimina un categoria por su ID")
    @ApiResponse(responseCode = "204", description = "Categoria eliminada correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontró la categoria")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<ApiResponseDto<Object>> delete(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Categoria eliminada correctamente", true, null);
    }

}
