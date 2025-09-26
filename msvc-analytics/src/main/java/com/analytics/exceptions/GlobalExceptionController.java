package com.analytics.exceptions;

import com.analytics.DTOs.ApiResponseDto;
import com.analytics.utils.ResponseBuilder;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.MethodNotAllowedException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        log.warn("Validation error: {}", details);
        return ResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Errores de validación en los campos enviados", false, details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Invalid request body: {}", ex.getMessage());
        return ResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "El formato del cuerpo de la solicitud es inválido", false, Collections.singletonList("Verifica la sintaxis JSON y los tipos de datos enviados"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        log.warn("Constraint violation: {}", details);
        return ResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Violación de restricciones en los datos", false, details);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleMethodNotAllowedException(MethodNotAllowedException ex) {
        List<String> supportedMethods = ex.getSupportedMethods() != null
                ? ex.getSupportedMethods().stream()
                .map(HttpMethod::name)
                .collect(Collectors.toList())
                : Collections.emptyList();

        log.warn("Method not allowed: {}", ex.getMessage());
        return ResponseBuilder.buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no soportado", false, supportedMethods);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("Resource not found: {}", ex.getReason());
        return ResponseBuilder.buildResponse(HttpStatus.NOT_FOUND, "Recurso no encontrado", false, Collections.singletonList("Motivo: " + ex.getReason()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied for user");
        return ResponseBuilder.buildResponse(HttpStatus.FORBIDDEN, "Acceso denegado", false, Collections.singletonList("No tienes los permisos necesarios para realizar esta acción"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage());
        return ResponseBuilder.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error en el servidor", false, Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleWebClientResponseException(WebClientResponseException ex) {

        log.error("Error calling external service: {}, status: {}", ex.getRequest().getURI(),
                ex.getStatusCode());
        return ResponseBuilder.buildResponse(HttpStatus.BAD_GATEWAY, "Error al comunicarse con un servicio externo", false,
                Collections.singletonList("Servicio: " + ex.getRequest().getURI() + ", Status: " + ex.getStatusCode()));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleCallNotPermittedException(CallNotPermittedException ex) {
        log.error("Circuit breaker abierto: {}", ex.getMessage());
        return ResponseBuilder.buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "El servicio solicitado no está disponible temporalmente", false,
                Collections.singletonList("Circuit breaker abierto: " + ex.getMessage()));
    }
}