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

    // Các bộ xử lý ngoại lệ khác có thể dùng các phương thức khác trong ExceptionBuilder
}