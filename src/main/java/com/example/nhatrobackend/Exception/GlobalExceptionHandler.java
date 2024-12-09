package com.example.nhatrobackend.Exception;

import com.example.nhatrobackend.DTO.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ExceptionBuilder.buildNotFoundExceptionResponse(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ExceptionBuilder.buildBadRequestExceptionResponse(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleIllegalAccessDeniedException(AccessDeniedException ex) {
        return ExceptionBuilder.buildBadRequestExceptionResponse(ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleIOException(IOException ex) {
        return ExceptionBuilder.buildIOExceptionResponse("Lỗi xảy ra khi upload file: " + ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        // Lấy thông báo lỗi đầu tiên từ danh sách các lỗi
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage()) // Chỉ lấy thông báo lỗi
                .findFirst()
                .orElse("Validation error");

        // Trả về lỗi ngắn gọn
        return ExceptionBuilder.buildBadRequestNotValidExceptionResponse(errorMessage);
    }

    // Xử lý RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleRuntimeException(RuntimeException ex) {
        return ExceptionBuilder.buildRuntimeExceptionResponse(ex.getMessage());
    }

}