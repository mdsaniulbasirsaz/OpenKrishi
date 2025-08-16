package com.openkrishi.OpenKrishi.domain.user.dto;

import com.openkrishi.OpenKrishi.domain.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDto {
    private String fullName;
    private String email;
    private String phone;
    private Double latitude;
    private Double longitude;
    public User.Role role;
    public User.Status status;
    public User.SubscriptionStatus subscriptionStatus;
    private String password;
}
