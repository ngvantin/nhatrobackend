package com.example.nhatrobackend.Exception;

import com.example.nhatrobackend.DTO.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.persistence.EntityNotFoundException;

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
}