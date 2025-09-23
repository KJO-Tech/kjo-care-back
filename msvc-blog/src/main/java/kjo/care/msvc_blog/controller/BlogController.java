package kjo.care.msvc_blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import kjo.care.msvc_blog.dto.*;
import kjo.care.msvc_blog.services.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
@Validated
@Log4j2
@SecurityRequirement(name = "securityToken")
@Tag(name = "Blog", description = "Operations for Blog")
public class BlogController {

    private final BlogService blogService;

    @Operation(summary = "Obtener todos los blogs", description = "Devuelve todos los blogs existentes")
    @ApiResponse(responseCode = "200", description = "Blogs obtenidas correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron Blogs")
    @PreAuthorize("hasRole('admin_client_role')")
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<BlogOverviewDto> response = blogService.findAllBlogs();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener todos los blogs Publicados", description = "Devuelve todos los blogs publicados ")
    @ApiResponse(responseCode = "200", description = "Blogs obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron Blogs")
    @GetMapping("/published")
    public ResponseEntity<?> findAllPublished() {
        List<BlogResponseDto> response = blogService.findAllBlogsPublished();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener todos los blogs Publicados", description = "Devuelve todos los blogs publicados ")
    @ApiResponse(responseCode = "200", description = "Blogs obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron Blogs")
    @GetMapping("")
    public ResponseEntity<BlogPageResponseDto> getAllPublishedBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        BlogPageResponseDto response = blogService.findBlogs(page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener Blog por ID", description = "Devuelve un blog por su ID")
    @ApiResponse(responseCode = "200", description = "Blog obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Blog no encontrado")
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        BlogResponseDto response = blogService.findBlogById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener Detalles de un Blog", description = "Devuelve los detalles de un blog")
    @ApiResponse(responseCode = "200", description = "Detalles obtenidos correctamente")
    @ApiResponse(responseCode = "404", description = "Detalles no encontrados")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailsById(@PathVariable UUID id) {
        BlogDetailsDto response = blogService.findBlogDetails(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear un Blog", description = "Crea una blog")
    @ApiResponse(responseCode = "201", description = "Blog creado correctamente")
    @ApiResponse(responseCode = "400", description = "No se pudo crear el blog")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@Parameter(description = "Datos del blog", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = BlogRequestDto.class)))
                                        @ModelAttribute @Validated BlogRequestDto blog,
                                        @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        BlogResponseDto createBlog = blogService.saveBlog(blog, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createBlog);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar un blog", description = "Actualiza solo los campos proporcionados")
    @ApiResponse(responseCode = "200", description = "Blog actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Blog no encontrada")
    public ResponseEntity<?> update(@PathVariable UUID id
            , @Parameter(description = "Datos del blog", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = BlogRequestDto.class)))
            @ModelAttribute @Validated BlogRequestDto blog
            , @AuthenticationPrincipal Jwt jwt) {
        String authenticatedUserId = jwt.getSubject();
        BlogResponseDto updated = blogService.updateBlog(id, blog,authenticatedUserId);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar una blog por ID", description = "Elimina un blog por su ID")
    @ApiResponse(responseCode = "204", description = "Blog eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontró el blog")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id,  @AuthenticationPrincipal Jwt jwt) {
        String authenticatedUserId = jwt.getSubject();
        blogService.deleteBlog(id, authenticatedUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener cantidad total de blogs", description = "Devuelve el número total de blogs publicados")
    @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente")
    @GetMapping("/count")
    public ResponseEntity<BlogCountDto> getBlogCount() {
        log.info("Petición para obtener cantidad total de blogs");
        Long count = blogService.countBlogs();
        log.info("Total de blogs: {}", count);
        return ResponseEntity.ok(new BlogCountDto(count));
    }

    @Operation(summary = "Obtener cantidad de blogs del mes anterior", description = "Devuelve el número de blogs publicados en el mes anterior")
    @ApiResponse(responseCode = "200", description = "Conteo del mes anterior obtenido correctamente")
    @GetMapping("/count/previous-month")
    public ResponseEntity<BlogCountDto> getPreviousMonthBlogs() {
        log.info("Petición para obtener cantidad de blogs del mes anterior");
        Long count = blogService.countBlogsPreviousMonth();
        log.info("Total de blogs del mes anterior: {}", count);
        return ResponseEntity.ok(new BlogCountDto(count));
    }

    @Operation(summary = "Obtener conteo de blogs por día entre fechas", description = "API interna para consultas de análisis")
    @ApiResponse(responseCode = "200", description = "Datos obtenidos correctamente")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/countByDay")
    public ResponseEntity<?> countBlogsByDay(
            @RequestParam String state,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Consulta: Contando blogs por día. State={}, startDate={}, endDate={}",
                state, startDate, endDate);

        try {
            List<Object[]> results = blogService.countBlogsByDayBetweenDates(state, startDate, endDate);

            List<Map<String, Object>> response = new ArrayList<>();
            for (Object[] result : results) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("date", result[0]);
                entry.put("count", result[1]);
                response.add(entry);
                log.debug("Resultado procesado: fecha={}, conteo={}", result[0], result[1]);
            }

            log.info("Resultados obtenidos: {} registros", results.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al procesar conteo de blogs por día: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar la consulta", "message", e.getMessage()));
        }
    }
}
