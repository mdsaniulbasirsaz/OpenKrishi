package com.openkrishi.OpenKrishi.domain.customer.mapper;

import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerProfileDto;
import com.openkrishi.OpenKrishi.domain.customer.entity.Customer;

public class CustomerProfileMapper {
    public static CustomerProfileDto toDto(Customer customer) {
        CustomerProfileDto dto = new CustomerProfileDto();
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setLatitude(customer.getLatitude());
        dto.setLongitude(customer.getLongitude());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }
} 