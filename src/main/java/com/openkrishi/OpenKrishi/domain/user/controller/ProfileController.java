package com.openkrishi.OpenKrishi.domain.user.controller;


import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.user.dto.ProfileUpdateRequestDto;
import com.openkrishi.OpenKrishi.domain.user.dto.UserProfileResponseDto;
import com.openkrishi.OpenKrishi.domain.user.entity.Profile;
import com.openkrishi.OpenKrishi.domain.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final JwtService jwtService;

    public ProfileController (ProfileService profileService, JwtService jwtService)
    {
        this.profileService = profileService;
        this.jwtService = jwtService;
    }

    //---------------GET PROFILE--------------
    @Operation(summary = "Get User Profile", description = "Fetch authenticated user's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {

            String jwt = token.replace("Bearer ", "");


            UUID userId = jwtService.extractUserId(jwt);


            UserProfileResponseDto profileDto = profileService.getUserProfile(userId);

            return ResponseEntity.ok(profileDto);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token.");
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @Operation(
            summary = "Update User Profile",
            description = "Update the logged-in user's profile including date of birth and profile image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                    @ApiResponse(responseCode = "401", description = "Invalid Token"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Image upload failed")
            }
    )
    @PostMapping("/me/update")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String token,
            @ModelAttribute ProfileUpdateRequestDto profileUpdateRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        try {
            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            profileService.updateProfile(userId, profileUpdateRequestDto, imageFile);

            return ResponseEntity.ok("Profile updated successfully.");

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed.");
        }
    }


}
