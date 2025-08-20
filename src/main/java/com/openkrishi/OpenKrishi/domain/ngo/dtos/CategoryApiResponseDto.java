package com.openkrishi.OpenKrishi.domain.ngo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryApiResponseDto<T> {
    private boolean success;
    private String message;

    @Schema(hidden = true)
    private T data;
}
