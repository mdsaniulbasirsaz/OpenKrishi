package com.openkrishi.OpenKrishi.domain.ngo.controller;


import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.AddressUpdateRequestDto;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.NgoUpdateRequestDto;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import com.openkrishi.OpenKrishi.domain.ngo.services.NgoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/ngos")
public class NgoController {

    private final NgoService ngoService;
    private final JwtService jwtService;

    public NgoController(NgoService ngoService, JwtService jwtService) {
        this.ngoService = ngoService;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "Update NGO details",
            description = "Updates the NGO manager name and/or licence PDF. " +
                    "Authorization token required in header. Returns success message if updated."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NGO updated successfully",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Bad request or validation error",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token",
                    content = @Content(schema = @Schema(type = "string")))
    })

    @PostMapping("/update")
    public ResponseEntity<?> updateNgo(
            @Parameter(description = "NGO update request with managerName field", required = true)
            @ModelAttribute NgoUpdateRequestDto requestDto,

            @Parameter(description = "Optional licence PDF file", required = false)
            @RequestParam(required = false) MultipartFile licencePdf,

            @Parameter(description = "Authorization header with Bearer token", required = true)
            @RequestHeader("Authorization") String token
    ) {
        try {
            ngoService.updateNgo(requestDto, licencePdf, token);
            return ResponseEntity.ok("NGO updated successfully");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Update NGO Address",
            description = "Update the address of a specific NGO. Authorization token required in header."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "NGO not found for user")
    })
    @PostMapping("/address/update")
    public ResponseEntity<?> updateNgoAddress(
            @RequestHeader("Authorization") String token,
            @RequestBody AddressUpdateRequestDto requestDto
    ) {
        try {

            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            String result = ngoService.updateNgoAddress(userId, requestDto);
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}


