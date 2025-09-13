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

import java.util.Optional;

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

        // Try to find User first
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Check if user is active, unless role is CUSTOMER
            if (!user.getRole().name().equals("CUSTOMER")
                    && !"ACTIVE".equalsIgnoreCase(user.getStatus().name())) {
                throw new RuntimeException("Invalid credentials"); // hide inactive info
            }

            // Check password
            if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }

            return jwtService.buildAuthResponse(
                    user.getEmail(),
                    user.getFullName(),
                    user.getId(),
                    user.getRole().name()
            );
        }

        // Try to find Customer
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        Customer customer = customerOpt.get();

        // Check password
        if (!passwordEncoder.matches(rawPassword, customer.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtService.buildAuthResponse(
                customer.getEmail(),
                customer.getFullName(),
                customer.getId(),
                "CUSTOMER"
        );
    }
}
