package com.openkrishi.OpenKrishi.domain.customer.dtos;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CustomerResponseDto {

    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SubscriptionResponseDto> subscriptions;
}
