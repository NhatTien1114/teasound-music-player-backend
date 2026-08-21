package com.teasound.teasound_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.teasound.teasound_api.dto.ErrorDto;
import com.teasound.teasound_api.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException e) {
        log.error("Error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(400)
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String enumKey = e.getFieldError().getDefaultMessage();
        ErrorDto errorDto = ErrorDto.valueOf(enumKey);
        log.error("Error: {}", e.getMessage());
        return ResponseEntity.status(errorDto.getHttpStatus()).body(ApiResponse.builder()
                .code(errorDto.getCode())
                .message(errorDto.getMessage())
                .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Error: {}", e.getMessage());
        ErrorDto errorDto = ErrorDto.valueOf(e.getMessage());
        return ResponseEntity.status(errorDto.getHttpStatus()).body(ApiResponse.builder()
                .code(errorDto.getCode())
                .message(errorDto.getMessage())
                .build());
    }

}
