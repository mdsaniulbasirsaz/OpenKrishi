package com.openkrishi.OpenKrishi.domain.customer.dtos;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponseDto {

    private UUID id;
    private String planName;
    private Float price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Float ngoShare;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByEmail;
}
