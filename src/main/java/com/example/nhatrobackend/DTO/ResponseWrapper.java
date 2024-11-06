package com.example.nhatrobackend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseWrapper<T> {
    private String status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseWrapper(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
