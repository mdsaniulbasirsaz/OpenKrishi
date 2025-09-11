package com.openkrishi.OpenKrishi.domain.user.controller;

import com.openkrishi.OpenKrishi.domain.auth.LoginServices.LoginService;
import com.openkrishi.OpenKrishi.domain.auth.dtos.AuthResponseDto;
import com.openkrishi.OpenKrishi.domain.auth.dtos.LoginDto;
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

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final LoginService loginService;

    public UserController(UserService userService, LoginService loginService)
    {
        this.userService = userService;
        this.loginService = loginService;
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

    // ----------Login User----------
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@RequestBody LoginDto loginDto) {
        AuthResponseDto authResponseDto = loginService.login(loginDto);
        return ResponseEntity.ok(authResponseDto);
    }

}
