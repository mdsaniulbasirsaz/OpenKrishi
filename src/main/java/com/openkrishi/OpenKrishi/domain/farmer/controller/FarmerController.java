package com.openkrishi.OpenKrishi.domain.farmer.controller;

import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.farmer.dtos.FarmerCreateRequestDto;
import com.openkrishi.OpenKrishi.domain.farmer.dtos.FarmerResponseDto;
import com.openkrishi.OpenKrishi.domain.farmer.entity.Farmer;
import com.openkrishi.OpenKrishi.domain.farmer.services.FarmerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("api/v1/farmers")
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
                    "- The `JWT` should contain the userId of the NGO owner.\n" +
                    "- Request body must contain farmer details including `FarmerName`, `phone`, `latitude`, and `longitude`.\n\n" +
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

    //--- Gel All Farmer-----------
    @Operation(
            summary = "Get all Farmers",
            description = "This API retrieves all farmers registered in the system.\n\n" +
                    "Flow:\n" +
                    "1. The system fetches all farmers from the database using `FarmerRepository`.\n" +
                    "2. If no farmers are available, it returns `404 Not Found` with a proper message.\n" +
                    "3. If farmers exist, it returns a list of farmer details (`id`, `farmerName`, `phone`, `latitude`, `longitude`, `ngoId`).\n\n" +
                    "Requirements:\n" +
                    "- `Authorization` header with Bearer JWT token must be provided.\n" +
                    "- The `JWT` should contain the userId of the NGO owner.\n\n" +
                    "Response Codes:\n" +
                    "- `200`: Successfully retrieved list of farmers.\n" +
                    "- `404`: No farmers found in the system.\n" +
                    "- `403 Forbidden`: `JWT` is invalid, missing, or the requester is not authorized.\n" +
                    "- `500`: Internal server error while fetching farmers."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of farmers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No farmers found in the system"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access - invalid or missing JWT"),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllFarmers() {
        try {
            List<FarmerResponseDto> farmers = farmerService.getAllFarmers();

            if (farmers.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("No farmers found in the system.");
            }

            return ResponseEntity.ok(farmers);

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred: " + ex.getMessage());
        }
    }

}
