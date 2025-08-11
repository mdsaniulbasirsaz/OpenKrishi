package com.openkrishi.OpenKrishi.domain.user.controller;

import com.openkrishi.OpenKrishi.domain.customer.services.CustomerAuthService;
import com.openkrishi.OpenKrishi.domain.user.dto.UserDto;
import com.openkrishi.OpenKrishi.domain.user.dto.UserResponseDto;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("v1/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<UserResponseDto> addUser(@RequestBody UserDto userDto) {
        if(userDto.role == User.Role.NGO){
            userDto.status = User.Status.INACTIVE;
        }else{
            userDto.status = User.Status.ACTIVE;
        }
        UserResponseDto createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
