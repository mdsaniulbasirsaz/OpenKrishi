package com.openkrishi.OpenKrishi.domain.user.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDto {
    private LocalDate dob;
}
