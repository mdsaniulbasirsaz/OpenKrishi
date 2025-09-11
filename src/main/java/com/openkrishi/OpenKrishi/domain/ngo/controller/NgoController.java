package com.openkrishi.OpenKrishi.domain.ngo.controller;


import com.openkrishi.OpenKrishi.domain.auth.dtos.ErrorResponseDto;
import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.*;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import com.openkrishi.OpenKrishi.domain.ngo.services.NgoService;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ngos")
public class NgoController {

    private static final Logger logger = LoggerFactory.getLogger(NgoController.class);


    private final NgoService ngoService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final NgoRepository ngoRepository;

    public NgoController(NgoRepository ngoRepository, NgoService ngoService, JwtService jwtService, UserRepository userRepository) {
        this.ngoService = ngoService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.ngoRepository = ngoRepository;
    }



    //-----------Create NGO------------------
    @Operation(
            summary = "Register a new NGO",
            description = """
        This API is used to register a new NGO with its associated `User` and `Address`.
        
        **Request Requirements:**
        - Send NGO information in **JSON body** (not form-data).
        - Include the license file as a **Base64 encoded string** in the field `licenceUrl`.
        
        **Process Flow:**
        - Validate that the email does not already exist.
        - Decode the Base64-encoded license file and upload it to Cloudinary (backend handles upload).
        - Create a new `User` with:
          - Role = `NGO`
          - Status = `INACTIVE`
        - Create and link the `NGO` entity to the `User`.
        - Add and associate the `Address` information.
        
        **Possible Errors:**
        - `400` Bad Request → Email already exists / Invalid input
        - `401` Unauthorized
        - `403` Forbidden
        - `500` Internal Server Error → Failure in Cloudinary upload or database save
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NGO created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, e.g., email already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error during creation or file upload")
    })
    @PostMapping(value = "/register/ngo")
    public ResponseEntity<?> createNgo(
           @RequestBody NgoCreateWithAddressDto ngoCreateWithAddressDto
    )  {

        try {
            if (userRepository.findByEmail(ngoCreateWithAddressDto.getEmail()).isPresent()) {
                // Email already exists
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "ERROR", "message", "Email already exists."));
            }

            ngoService.createNgoWithAddress(ngoCreateWithAddressDto);

            // Successfully created
            return ResponseEntity.ok(
                    Map.of("status", "SUCCESS", "message", "Successfully Created.")
            );
        } catch (Exception e) {
            logger.error("Failed to create NGO: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "status", "ERROR",
                                    "message", "Failed to create NGO: " + e.getMessage()
                            )
                    );
        }
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


    //----------------NGO member Create Controller------------------
    @Operation(
            summary = "Create a new member for NGO",
            description = """
        Adds a new member to the authenticated NGO.

        - **Authorization**: Requires a valid JWT Bearer token in the `Authorization` header.
        - **NGO Identification**: Automatically determined from the JWT token of the requester.
        - **Member Details**: Provide name, phone, address, memberDesignation, and optional image in the request body.
        - **Preconditions**:
            - The NGO must be active.
            - The member's email (if used) or identity should not already exist in the NGO.
        - **Responses**:
            - `200 OK`: Member added successfully.
            - `400 Bad Request`: Invalid input or validation failed.
            - `403 Forbidden`: JWT is invalid, missing, or the requester is not authorized.
        """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Member added successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request or Validation Failed",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Invalid JWT or Not Authorized")
            }
    )
    @PostMapping("/create/member")
    public ResponseEntity<?> createMember(
            @RequestBody CreateMemberDto createMemberDto
    ) {
        try {
            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            UUID userId = user.getId();

            // Call service
            CreateMemberResponseDto responseDto = ngoService.createMember(userId, createMemberDto);

            return ResponseEntity.ok(responseDto);

        } catch (RuntimeException e) {
            logger.error("Failed to create member: {}", e.getMessage(), e);
            ErrorResponseDto error = new ErrorResponseDto(
                    400,
                    e.getMessage(),
                    e.getClass().getSimpleName(),
                    System.currentTimeMillis()
            );
            return ResponseEntity.status(400).body(error);
        } catch (Exception e) {
            logger.error("Unexpected error creating member: {}", e.getMessage(), e);
            ErrorResponseDto error = new ErrorResponseDto(
                    500,
                    "Internal server error while creating member",
                    e.getClass().getSimpleName(),
                    System.currentTimeMillis()
            );
            return ResponseEntity.status(500).body(error);
        }
    }


    @GetMapping("/all/ngo")
    @Operation(
            summary = "Retrieve all registered NGOs",
            description = "This API endpoint retrieves a complete list of all registered NGOs in the system along with their associated User and Address information.\n\n" +
                    "Behavior:\n" +
                    "1. The system fetches all Users with role `NGO`.\n" +
                    "2. For each User, it retrieves the linked `Ngo` entity.\n" +
                    "3. From the `Ngo` entity, the associated `Address` is fetched.\n" +
                    "4. Only the following fields are returned for security and clarity:\n" +
                    "   - User: `id`, `fullName`, `email`, `phone`, `latitude`, `longitude`\n" +
                    "   - Ngo: `managerName`, `licenceUrl`\n" +
                    "   - Address: `street`, `houseNo`, `city`, `state`, `postCode`, `village`\n\n" +
                    "Error Handling:\n" +
                    "- If an unexpected error occurs (e.g., database issue), the API returns HTTP status `500`.\n" +
                    "- If the request is unauthorized, it returns `401 Unauthorized`.\n" +
                    "- If the request is forbidden due to access control, it returns `403 Forbidden`.\n" +
                    "- The response contains a structured JSON error object with fields:\n" +
                    "   - `status`: HTTP status code\n" +
                    "   - `message`: Description of the error\n" +
                    "   - `error`: Exception class name\n" +
                    "   - `timestamp`: Epoch time of the error\n\n" +
                    "Notes:\n" +
                    "- Passwords, roles, subscription status, and other sensitive user fields are excluded.\n" +
                    "- The endpoint is read-only and does not modify any data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all NGOs"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You do not have permission to access this resource"),
            @ApiResponse(responseCode = "500", description = "Internal server error while fetching NGOs")
    })
    public ResponseEntity<?> getAllNgos()
    {
        try{
            List<NgoResponseDto> ngos = ngoService.FindAllNgos();
            return ResponseEntity.ok(ngos);
        } catch (Exception e)
        {
            logger.error("Failed to Get All NGO: {}", e.getMessage(), e);
            ErrorResponseDto error = new ErrorResponseDto(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to fetch NGOs",
                    e.getClass().getSimpleName(),
                    System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    //-------------Member List By Ngo---------
    @Operation(
            summary = "Get all members of the logged-in user's NGO",
            description = """
        This API endpoint retrieves all members of the NGO associated with the logged-in user.
        
        Requirements:
        - Authorization header with Bearer JWT token must be provided.
        - The `JWT` should contain the userId of the `NGO owner`.
        
        Behavior:
        - The NGO is determined automatically from the logged-in user's userId.
        - Returns a list of all members under that NGO.
        
        Response Codes:
        - `200` OK: Returns a list of MemberResponseDto objects.
        - `400` Bad Request: Invalid input or user not found.
        - `403` Forbidden: JWT is invalid, missing, or the requester is not authorized.
        """
    )
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponseDto>> getAllMembers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        UUID userId = user.getId();

        List<MemberResponseDto> members = ngoService.getAllMembers(userId);
        return ResponseEntity.ok(members);
    }




}


