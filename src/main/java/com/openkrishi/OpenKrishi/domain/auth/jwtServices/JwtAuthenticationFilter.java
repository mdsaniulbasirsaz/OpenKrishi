package com.openkrishi.OpenKrishi.domain.auth.jwtServices;

import com.openkrishi.OpenKrishi.domain.customer.repository.CustomerRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomerRepository customerRepository;

    @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    String path = request.getServletPath();
    if (path.equals("/v1/api/customer/auth/register") || path.equals("/v1/api/customer/auth/login")) {
        filterChain.doFilter(request, response);
        return;
    }

    String authHeader = request.getHeader("Authorization");
    String token = null;
    String email = null;
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
        try {
            email = jwtService.getEmailFromToken(token);
        } catch (JwtException e) {
            // Invalid token
        }
    }
    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//        var customerOpt = customerRepository.findByEmail(email);

        var userOpt = customerRepository.findByEmail(email)
                .map(user -> (UserDetails) User.withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(Collections.emptyList())
                        .build());
//                .or(() -> ngoRepository.findByEmail(email)
//                        .map(user -> (UserDetails) User.withUsername(user.getEmail())
//                                .password(user.getPassword())
//                                .authorities(Collections.emptyList())
//                                .build()))
//                .or(() -> farmerRepository.findByEmail(email)
//                        .map(user -> (UserDetails) User.withUsername(user.getEmail())
//                                .password(user.getPassword())
//                                .authorities(Collections.emptyList())
//                                .build()));

        if (userOpt.isPresent() && jwtService.validateToken(token)) {
            UserDetails userDetails = User.withUsername(email)
                    .password(userOpt.get().getPassword())
                    .authorities(Collections.emptyList())
                    .build();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
    filterChain.doFilter(request, response);
}
} 