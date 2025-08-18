package com.openkrishi.OpenKrishi.domain.admin.controller;

import com.openkrishi.OpenKrishi.domain.admin.services.NgoStatusUpdateServices;
import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.openkrishi.OpenKrishi.domain.admin.entity.Profile;
import com.openkrishi.OpenKrishi.domain.admin.repository.ProfileRepository;


@RestController
@RequestMapping("v1/admin")
public class AdminController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private NgoStatusUpdateServices ngoStatusUpdateServices;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profiles")
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @GetMapping("/success")
    public String Success() {
        return "Swagger Setup Success";
    }


    //---------------Update NGO Status By Email-----------------
    @Operation(
            summary = "Activate NGO Account",
            description = "This API allows an authenticated admin to activate a NGO user account.<br>" +
                    "The admin must provide a valid JWT token in the 'Authorization' header using the Bearer scheme.<br>" +
                    "The email of the target NGO user must be passed as a query parameter.<br>" +
                    "If the email belongs to a valid NGO user, their status will be updated to ACTIVE.<br>" +
                    "Only users with the ADMIN role are authorized to perform this action.<br>" +
                    "The endpoint will return:<br>" +
                    "- 200 OK: Successful activation<br>" +
                    "- 403 Forbidden: Caller is not an admin<br>" +
                    "- 401 Unauthorized: Invalid token<br>" +
                    "- 404 Not Found: No NGO exists with the given email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NGO activated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden: not an admin"),
            @ApiResponse(responseCode = "404", description = "NGO not found or invalid role")
    })
    @PostMapping("/ngo/updateStatus")
    @Transactional
    public ResponseEntity<String> updateNgoStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam String email) {

        try {
            // Extract JWT from header
            String jwt = token.replace("Bearer ", "");

            // Extract userId from JWT
            UUID adminId = jwtService.extractUserId(jwt);

            // Verify admin role
            Optional<User> adminUserOpt = userRepository.findById(adminId);
            if (adminUserOpt.isEmpty() || adminUserOpt.get().getRole() != User.Role.ADMIN) {
                return ResponseEntity.status(403).body("Forbidden: Only admins can perform this action.");
            }

            // Activate NGO
            boolean updated = ngoStatusUpdateServices.activeNgoByEmail(email);
            if (updated) {
                return ResponseEntity.ok("NGO with email " + email + " has been activated successfully.");
            } else {
                return ResponseEntity.status(404).body("NGO with email " + email + " not found or not an NGO.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token.");
        }
    }
}
