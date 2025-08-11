package com.openkrishi.OpenKrishi.domain.auth.LoginServices;

import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.customer.dtos.AuthResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerLoginDto;
import com.openkrishi.OpenKrishi.domain.customer.entity.Customer;
import com.openkrishi.OpenKrishi.domain.customer.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

        Optional<?> userOpt = customerRepository.findByEmail(email)
                .map(user -> (Object) user);
//                .or(() -> ngoRepository.findByEmail(email).map(user -> (Object) user))
//                .or(() -> farmerRepository.findByEmail(email).map(user -> (Object) user));

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        Object user = userOpt.get();

        String storedPassword;
        String fullName;

        Customer customer = (Customer) user;
        storedPassword = customer.getPassword();
        fullName = customer.getFullName();

        if (!passwordEncoder.matches(rawPassword, storedPassword)) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtService.buildAuthResponse(email, fullName);
    }
}
