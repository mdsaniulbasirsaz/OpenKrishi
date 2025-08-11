package com.openkrishi.OpenKrishi.domain.user.service;

import com.openkrishi.OpenKrishi.domain.user.dto.UserDto;
import com.openkrishi.OpenKrishi.domain.user.dto.UserResponseDto;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto createUser(UserDto userDto) {
        User user = new User();

        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setPhone(userDto.getPhone());
        user.setLatitude(userDto.getLatitude());
        user.setLongitude(userDto.getLongitude());
        user.setRole(userDto.getRole());
        user.setStatus(userDto.getStatus());

        User savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .id(savedUser.getId().toString())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .latitude(savedUser.getLatitude())
                .longitude(savedUser.getLongitude())
                .role(savedUser.getRole())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
    }
}
