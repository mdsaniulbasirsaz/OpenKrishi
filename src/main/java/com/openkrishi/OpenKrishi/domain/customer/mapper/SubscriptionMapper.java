package com.openkrishi.OpenKrishi.domain.customer.mapper;
import com.openkrishi.OpenKrishi.domain.customer.dtos.SubscriptionCreateDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.SubscriptionResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.entity.Customer;
import com.openkrishi.OpenKrishi.domain.customer.entity.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public SubscriptionResponseDto toDto(Subscription subscription) {
        return SubscriptionResponseDto.builder()
                .id(subscription.getId())
                .planName(subscription.getPlanName())
                .price(subscription.getPrice())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .ngoShare(subscription.getNgoShare())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .createdByEmail(subscription.getCreatedByEmail()) //Here Get Customer -> GetNgo Email
                .build();
    }

    public  Subscription toEntity(SubscriptionCreateDto dto, Customer customer, String createdByEmail ) {
        return Subscription.builder()
                .planName(dto.getPlanName())
                .price(dto.getPrice())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .ngoShare(dto.getNgoShare())
                .createdByEmail(createdByEmail)
                .customer(null)
                .build();
    }
}
