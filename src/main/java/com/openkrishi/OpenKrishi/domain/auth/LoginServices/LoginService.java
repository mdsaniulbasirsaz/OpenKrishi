package com.openkrishi.OpenKrishi.domain.auth.LoginServices;

import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.auth.dtos.AuthResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerLoginDto;
import com.openkrishi.OpenKrishi.domain.customer.entity.Customer;
import com.openkrishi.OpenKrishi.domain.customer.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class LoginService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginService(
        CustomerRepository customerRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDto login(CustomerLoginDto loginDto) {
        String email = loginDto.getEmail();
        String rawPassword = loginDto.getPassword();


        Optional<Customer> customerOpt = customerRepository.findByEmail(email);

        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        Customer customer = customerOpt.get();

        // Check password
        if (!passwordEncoder.matches(rawPassword, customer.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Build response
        return jwtService.buildAuthResponse(
                customer.getEmail(),
                customer.getFullName(),
                customer.getId()
        );
    }
}
