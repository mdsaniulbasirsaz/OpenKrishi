package com.openkrishi.OpenKrishi.domain.customer.controller;

import com.openkrishi.OpenKrishi.domain.customer.dtos.AuthResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerLoginDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerRegisterDto;
import com.openkrishi.OpenKrishi.domain.customer.services.CustomerAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/customer/auth")
public class CustomerAuthController {
    private final CustomerAuthService authService;

    public CustomerAuthController(CustomerAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody CustomerRegisterDto registerDto) {
//        System.out.println("Received Register DTO: " + registerDto);
        AuthResponseDto response = authService.register(registerDto);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody CustomerLoginDto loginDto) {
        
        AuthResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
} 