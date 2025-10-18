package kjo.care.msvc_blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kjo.care.msvc_blog.dto.*;
import kjo.care.msvc_blog.dto.ReactionDtos.ReactionRequestDto;
import kjo.care.msvc_blog.dto.ReactionDtos.ReactionResponseDto;
import kjo.care.msvc_blog.services.ReactionService;
import kjo.care.msvc_blog.utils.ResponseBuilder;
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
@RequestMapping("/reaction")
@RequiredArgsConstructor
@Validated
@Log4j2
@SecurityRequirement(name = "securityToken")
@Tag(name = "Reaction", description = "Operations for Reaction")
public class ReactionController {

    private final ReactionService reactionService;

    @Operation(summary = "Obtener todos las Reacciones", description = "Devuelve todos las Reacciones existentes")
    @ApiResponse(responseCode = "200", description = "Reacciones obtenidas correctamente")
    @ApiResponse(responseCode = "204", description = "No se encontraron Reacciones")
    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<List<ReactionResponseDto>>> findAll() {
        List<ReactionResponseDto> response = reactionService.findAllReactions();
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Reacciones obtenidas correctamente", true, response);
    }

    @Operation(summary = "Crear una Reaccion", description = "Crea una Reaccion")
    @ApiResponse(responseCode = "201", description = "Like creada correctamente")
    @ApiResponse(responseCode = "400", description = "No se pudo dar like")
    @PostMapping("")
    public ResponseEntity<ApiResponseDto<ReactionResponseDto>> create(@RequestBody @Validated ReactionRequestDto reaction, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        ReactionResponseDto create = reactionService.saveReaction(reaction, userId);
        return ResponseBuilder.buildResponse(HttpStatus.CREATED, "Reaccion creada correctamente", true, create);
    }

    @Operation(summary = "Eliminar reaccion", description = "Elimina una Reaccion")
    @ApiResponse(responseCode = "204", description = "Reaccion eliminada correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontró la reaccion")
    @DeleteMapping("/{blogId}")
    public ResponseEntity<ApiResponseDto<Object>> delete(@PathVariable UUID blogId, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        reactionService.deleteReaction(blogId, userId);
        return ResponseBuilder.buildResponse(HttpStatus.OK, "Reaccion eliminada correctamente", true, null);
    }

}
