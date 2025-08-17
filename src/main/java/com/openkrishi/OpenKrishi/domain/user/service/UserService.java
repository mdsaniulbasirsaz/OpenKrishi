package com.openkrishi.OpenKrishi.domain.user.service;

import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.auth.dtos.AuthResponseDto;
import com.openkrishi.OpenKrishi.domain.user.dto.UserDto;
import com.openkrishi.OpenKrishi.domain.user.dto.UserResponseDto;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto createUser(UserDto userDto) {

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();

        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setPhone(userDto.getPhone());
        user.setLatitude(userDto.getLatitude());
        user.setLongitude(userDto.getLongitude());
        user.setRole(userDto.getRole());
        user.setIsSubscribed(userDto.getSubscriptionStatus());
        user.setStatus(userDto.getStatus());

        User savedUser = userRepository.save(user);



        AuthResponseDto authResponse = jwtService.buildAuthResponse(
                user.getEmail(),
                user.getFullName(),
                user.getId()
        );

        return UserResponseDto.builder()
                .id(savedUser.getId().toString())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .latitude(savedUser.getLatitude())
                .longitude(savedUser.getLongitude())
                .role(savedUser.getRole())
                .subscriptionStatus(savedUser.getIsSubscribed())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .token(authResponse.getToken())
                .build();


    }


}
