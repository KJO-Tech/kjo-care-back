package kjo.care.msvc_auth.util;

import kjo.care.msvc_auth.dto.ApiResponseDto;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseBuilder {
    public <T> ResponseEntity<ApiResponseDto<T>> buildResponse(HttpStatus status, String message, boolean isSuccess, T result) {
        ApiResponseDto<T> response = ApiResponseDto.<T>builder()
                .statusCode(status.value())
                .isSuccess(isSuccess)
                .message(message)
                .result(result)
                .build();
        return ResponseEntity.status(status).body(response);
    }

}
