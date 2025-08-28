package com.openkrishi.OpenKrishi.domain.ngo.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.AddressUpdateRequestDto;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.NgoCreateWithAddressDto;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.NgoUpdateRequestDto;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Member;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import com.openkrishi.OpenKrishi.domain.ngo.services.MemberService;
import com.openkrishi.OpenKrishi.domain.ngo.services.NgoService;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final MemberService memberService;
    private final UserRepository userRepository;

    public NgoController(NgoService ngoService, JwtService jwtService, UserRepository userRepository, MemberService memberService) {
        this.ngoService = ngoService;
        this.jwtService = jwtService;
        this.memberService = memberService;
        this.userRepository = userRepository;
    }



    //-----------Create NGO------------------
    @Operation(
            summary = "Register a new NGO",
            description = "This API allows creating a new NGO along with its associated `User` and `Address`.\n" +
                    "\nRequirements:\n" +
                    "- Provide NGO information in JSON format.\n" +
                    "- Provide a valid license file (MultipartFile).\n" +
                    "\nProcess:\n" +
                    "- Check if the email already exists.\n" +
                    "- Upload the license file to Cloudinary.\n" +
                    "- Create a `User` with role `NGO` and status `INACTIVE`.\n" +
                    "- `NGO` entity and associate it with the `User`.\n" +
                    "- Set the address\n" +
                    "\nExample JSON for `info`:\n" +
                    "```\n" +
                    "{\n" +
                    "  \"fullName\": \"John Doe NGO\",\n" +
                    "  \"email\": \"john@example.com\",\n" +
                    "  \"password\": \"securePass123\",\n" +
                    "  \"phone\": \"0123456789\",\n" +
                    "  \"managerName\": \"John Doe\",\n" +
                    "  \"latitude\": 23.7808875,\n" +
                    "  \"longitude\": 90.2792371,\n" +
                    "  \"address\": {\n" +
                    "    \"street\": \"Street 1\",\n" +
                    "    \"houseNo\": \"12A\",\n" +
                    "    \"city\": \"Dhaka\",\n" +
                    "    \"state\": \"Dhaka\",\n" +
                    "    \"postCode\": \"1207\",\n" +
                    "    \"village\": \"Example Village\"\n" +
                    "  }\n" +
                    "}\n" +
                    "```\n" +
                    "\nPossible Errors:\n" +
                    "- `400` Bad Request: Email already exists or invalid input.\n" +
                    "- `500` Internal Server Error: Failure while saving data or uploading license."+
                    "- `403` Forbidden."

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NGO created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, e.g., email already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error during creation or file upload")
    })
    @PostMapping(value = "/register/ngo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNgo(
            @RequestPart("licence") MultipartFile licenceFile,
            @RequestPart("info") String infoJson
    ) throws IOException {

        // JSON parse
        ObjectMapper mapper = new ObjectMapper();
        NgoCreateWithAddressDto ngoCreateWithAddressDto = mapper.readValue(infoJson, NgoCreateWithAddressDto.class);

        // Email check
        if (userRepository.findByEmail(ngoCreateWithAddressDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists.");
        }

        // Service call
        ngoService.createNgoWithAddress(ngoCreateWithAddressDto, licenceFile);

        return ResponseEntity.ok("Successfully Created.");
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


    //----------------NGO Create Controller------------------
    @Operation(
            summary = "Create a new member for NGO",
            description = """
            This API endpoint allows the NGO owner to add a new member to their NGO. 
    
            - **Authorization**: Requires a valid JWT Bearer token in the `Authorization` header.
            - **NGO Identification**: The NGO is automatically determined based on the userId present in the JWT token.
            - **Member Email**: The email of the user to be added as a member must be provided as a query parameter.
            - **Member Designation**: The role/designation of the new member (e.g., VOLUNTEER, DELIVERY_AGENT, FARMER_MANAGER) should be provided in the request body.
            - **Preconditions**:
                - The NGO owner (from JWT) must have active status.
                - The user being added must exist and should not already be a member of the NGO.
            - **Responses**:
                - `200 OK`: Member added successfully.
                - `400 Bad Request`: Invalid input, user not found, or user already a member.
                - `403 Forbidden`: JWT is invalid, missing, or the requester is not authorized.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Member Added Successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request or Validation Failed",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Invalid JWT or Not Authorized")
            }
    )
    @PostMapping("/create/member")
    public ResponseEntity<?> createMember(
            @RequestHeader ("Authorization") String token,
            @RequestParam String email,
            @RequestBody MemberCreateRequest request
    ){
        try {
            String jwt = token.replace("Bearer ", "");
            UUID ngoId = jwtService.extractUserId(jwt);

            // Call Service
            Member member = memberService.createMember(email,ngoId, request.getDesignation());

            return  ResponseEntity.ok("Member Added Successfully Complete.");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Data
    public static class MemberCreateRequest {
        private Member.MemberDesignation designation;
    }

}


