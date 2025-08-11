package com.openkrishi.OpenKrishi.domain.customer.services;

import com.openkrishi.OpenKrishi.domain.auth.LoginServices.LoginService;
import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.customer.dtos.AuthResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerLoginDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerRegisterDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerProfileDto;
import com.openkrishi.OpenKrishi.domain.customer.entity.Customer;
import com.openkrishi.OpenKrishi.domain.customer.mapper.CustomerProfileMapper;
import com.openkrishi.OpenKrishi.domain.customer.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerAuthService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginService loginService;


    public CustomerAuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, JwtService jwtService, LoginService loginService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loginService = loginService;
    }

    public AuthResponseDto register(CustomerRegisterDto registerDto) {
        if (customerRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        Customer customer = new Customer();
        customer.setEmail(registerDto.getEmail());
        customer.setFullName(registerDto.getFullName());
        customer.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        customer.setPhone(registerDto.getPhone());
        customer.setLatitude(registerDto.getLatitude());
        customer.setLongitude(registerDto.getLongitude());
        customerRepository.save(customer);
        return jwtService.buildAuthResponse(customer.getEmail(), customer.getFullName());
    }



    // Re-added login() method
    public AuthResponseDto login(CustomerLoginDto loginDto) {
        return loginService.login(loginDto);
    }

    public CustomerProfileDto getProfileByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer with email '" + email + "' not found"));
        return CustomerProfileMapper.toDto(customer);
    }
} 