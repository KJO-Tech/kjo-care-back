package kjo.care.msvc_emergency.exceptions;

import jakarta.validation.ConstraintViolationException;
import kjo.care.msvc_emergency.dto.ApiResponseDto;
import kjo.care.msvc_emergency.utils.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
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
        return ResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Errores de validación en los campos enviados", false, Collections.singletonList(details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Invalid request body: {}", ex.getMessage());
        return ResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "El formato del cuerpo de la solicitud es inválido", false, Collections.singletonList("Verifica la sintaxis JSON y los tipos de datos enviados"));
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleInvalidDataAccessApiUsage(InvalidDataAccessApiUsageException ex) {
        log.warn("Invalid data access: {}", ex.getMessage());
        return ResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Error en los parámetros de consulta", false, Collections.singletonList(ex.getMostSpecificCause().getMessage()));
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

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        List<String> supportedMethods = ex.getSupportedHttpMethods() != null ?
                ex.getSupportedHttpMethods().stream().map(Object::toString).collect(Collectors.toList()) :
                Collections.emptyList();

        log.warn("Method not allowed: {} for path {}", ex.getMethod(), ex.getMessage());
        return ResponseBuilder.buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no soportado: " + ex.getMethod(), false, Collections.singletonList("Métodos soportados: " + String.join(", ", supportedMethods)));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("Resource not found: {}", ex.getRequestURL());
        return ResponseBuilder.buildResponse(HttpStatus.NOT_FOUND, "El recurso solicitado no existe", false, Collections.singletonList("Path: " + ex.getRequestURL()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied for user");
        return ResponseBuilder.buildResponse(HttpStatus.FORBIDDEN, "Acceso denegado", false, Collections.singletonList("No tienes los permisos necesarios para realizar esta acción"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage());
        return ResponseBuilder.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error en el servidor", false, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleMoodEntityNodFoundException(EntityNotFoundException ex) {
        log.warn("Mood not found:{}", ex.getMessage());
        return ResponseBuilder.buildResponse(HttpStatus.NOT_FOUND, "Ha ocurrido un error con el BLOG", false, Collections.singletonList(ex.getMessage()));
    }

}
