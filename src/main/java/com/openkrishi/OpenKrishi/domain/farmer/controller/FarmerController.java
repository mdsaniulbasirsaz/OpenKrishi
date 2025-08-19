package com.openkrishi.OpenKrishi.domain.farmer.controller;

import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.farmer.dtos.FarmerCreateRequestDto;
import com.openkrishi.OpenKrishi.domain.farmer.dtos.FarmerResponseDto;
import com.openkrishi.OpenKrishi.domain.farmer.entity.Farmer;
import com.openkrishi.OpenKrishi.domain.farmer.services.FarmerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/farmers")
public class FarmerController {

    private final FarmerService farmerService;
    private final JwtService jwtService;

    public FarmerController(
            FarmerService farmerService,
            JwtService jwtService
    ) {
        this.farmerService = farmerService;
        this.jwtService = jwtService;
    }


    // ----------------Create Farmer---------------
    @Operation(
            summary = "Create a new Farmer",
            description = "This API creates a new farmer under the logged-in member's `NGO`.\n" +
                    "Requirements:\n" +
                    "- `Authorization` header with Bearer JWT token must be provided.\n" +
                    "- The `JWT` should contain the userId of the member.\n" +
                    "- Request body must contain farmer details including `FarmerName`, `phone`, `latitude`, and `longitude`.\n\n" +
                    "Behavior:\n" +
                    "- The createdBy field of the farmer is set to the member associated with the JWT.\n" +
                    "- The ngo field is automatically set to the member's NGO.\n" +
                    "- On success, returns a FarmerResponseDto with all relevant information.\n\n" +
                    "Response Codes:\n" +
                    "- `200`: Farmer created successfully.\n" +
                    "- `400`: Bad request or member not found.\n" +
                    "- `403` Forbidden: `JWT` is invalid, missing, or the requester is not authorized."
    )
    @PostMapping("/create")
    public ResponseEntity<?> createFarmer(
        @RequestHeader("Authorization") String token,
        @RequestBody FarmerCreateRequestDto farmerCreateRequestDto
    ){
        try{
            String jwt = token.replace("Bearer ", "");

            UUID userId = jwtService.extractUserId(jwt);

            FarmerResponseDto responseDto = farmerService.createFarmer(userId, farmerCreateRequestDto);
            return ResponseEntity.ok(responseDto);
        } catch(Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
