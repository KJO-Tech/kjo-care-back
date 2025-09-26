package com.analytics.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {
    private int statusCode;
    private boolean isSuccess;
    private String message;
    private T result;
}
