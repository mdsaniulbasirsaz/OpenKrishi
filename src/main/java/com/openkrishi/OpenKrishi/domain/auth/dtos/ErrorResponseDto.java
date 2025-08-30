package com.openkrishi.OpenKrishi.domain.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private int status;        // HTTP status code
    private String message;
    private String error;
    private long timestamp;
}
