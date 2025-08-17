package com.openkrishi.OpenKrishi.domain.auth.LoginServices;

import com.openkrishi.OpenKrishi.domain.auth.dtos.LoginDto;
import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.auth.dtos.AuthResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.entity.Customer;
import com.openkrishi.OpenKrishi.domain.customer.repository.CustomerRepository;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    public LoginService(
        CustomerRepository customerRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        UserRepository userRepository
    ) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public AuthResponseDto login(LoginDto loginDto) {
        String email = loginDto.getEmail();
        String rawPassword = loginDto.getPassword();

        // User Find
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
            return jwtService.buildAuthResponse(user.getEmail(), user.getFullName(), user.getId());
        }
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
