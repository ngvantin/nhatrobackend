package com.example.nhatrobackend.Exception;

import com.example.nhatrobackend.DTO.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExceptionBuilder {

    // Phương thức static để trả về ResponseEntity với lỗi BAD_REQUEST
    public static ResponseEntity<ResponseWrapper<Void>> buildNotFoundExceptionResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.<Void>builder()
                        .status("error")
                        .message(message)
                        .build());
    }

    // Phương thức để trả về phản hồi với lỗi BAD_REQUEST cho IllegalArgumentException
    public static ResponseEntity<ResponseWrapper<Void>> buildBadRequestExceptionResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.<Void>builder()
                        .status("error")
                        .message(message)
                        .build());
    }

    public static ResponseEntity<ResponseWrapper<Void>> handleIllegalAccessDeniedException(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.<Void>builder()
                        .status("error")
                        .message(message)
                        .build());
    }

    public static ResponseEntity<ResponseWrapper<Void>> buildIOExceptionResponse(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseWrapper.<Void>builder()
                        .status("error")
                        .message(message)
                        .build());
    }

}