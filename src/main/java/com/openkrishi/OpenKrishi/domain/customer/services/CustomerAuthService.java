package com.openkrishi.OpenKrishi.domain.customer.services;

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

    public CustomerAuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    public AuthResponseDto login(CustomerLoginDto loginDto) {
        Customer customer = customerRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(loginDto.getPassword(), customer.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtService.buildAuthResponse(customer.getEmail(), customer.getFullName());
    }

    public CustomerProfileDto getProfileByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer with email '" + email + "' not found"));
        return CustomerProfileMapper.toDto(customer);
    }
} 