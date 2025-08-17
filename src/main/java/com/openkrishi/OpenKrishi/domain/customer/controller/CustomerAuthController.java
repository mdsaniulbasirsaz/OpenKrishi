package com.openkrishi.OpenKrishi.domain.customer.controller;

import com.openkrishi.OpenKrishi.domain.auth.dtos.AuthResponseDto;
import com.openkrishi.OpenKrishi.domain.auth.dtos.LoginDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerLoginDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerRegisterDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerProfileDto;
import com.openkrishi.OpenKrishi.domain.customer.services.CustomerAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("v1/api/customer/auth")
public class CustomerAuthController {
    private final CustomerAuthService authService;


    public CustomerAuthController(CustomerAuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody CustomerRegisterDto registerDto) {
//        System.out.println("Received Register DTO: " + registerDto);
        AuthResponseDto response = authService.register(registerDto);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        
        AuthResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileDto> getProfile(Authentication authentication) {
        String email = authentication.getName();
        CustomerProfileDto profile = authService.getProfileByEmail(email);
        return ResponseEntity.ok(profile);
    }
} 