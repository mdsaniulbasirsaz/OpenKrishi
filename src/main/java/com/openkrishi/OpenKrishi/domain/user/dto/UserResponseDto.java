package com.openkrishi.OpenKrishi.domain.user.dto;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserResponseDto {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private Double latitude;
    private Double longitude;
    private User.Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
