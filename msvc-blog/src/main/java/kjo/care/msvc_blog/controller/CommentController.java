package kjo.care.msvc_blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import kjo.care.msvc_blog.dto.CommentRequestDto;
import kjo.care.msvc_blog.dto.CommentResponseDto;
import kjo.care.msvc_blog.services.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Validated
@Log4j2
@SecurityRequirement(name = "securityToken")
@Tag(name = "Comment", description = "Operations for Comment")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Obtener todos los Comentarios", description = "Devuelve todos los comentarios existentes")
    @ApiResponse(responseCode = "200", description = "Comentarios obtenidos correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron Comentarios")
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<CommentResponseDto> response = commentService.findAllComments();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear un Comentario", description = "Crea un comentario")
    @ApiResponse(responseCode = "201", description = "Comentario creado correctamente")
    @ApiResponse(responseCode = "400", description = "No se pudo crear el comentario")
    @PostMapping()
    public ResponseEntity<?> create(@ModelAttribute @Validated CommentRequestDto comment, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        CommentResponseDto createComment = commentService.saveComment(comment, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createComment);
    }

    @PutMapping(path = "/{id}")
    @Operation(summary = "Actualizar un comentario", description = "Actualiza solo los campos proporcionados")
    @ApiResponse(responseCode = "200", description = "Comentario actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Comentario no encontrado")
    public ResponseEntity<?> update(@PathVariable UUID id, @ModelAttribute @Validated CommentRequestDto comment, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        CommentResponseDto updated = commentService.updateComment(id, comment,userId);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar una comentario por ID", description = "Elimina un comentario por su ID")
    @ApiResponse(responseCode = "204", description = "Comentario eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontró el Comentario")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id,  @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }
}
