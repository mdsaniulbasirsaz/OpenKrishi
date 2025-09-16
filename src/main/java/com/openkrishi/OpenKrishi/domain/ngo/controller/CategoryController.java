package com.openkrishi.OpenKrishi.domain.ngo.controller;


import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.CategoryApiResponseDto;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.CategoryCreateDto;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Category;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import com.openkrishi.OpenKrishi.domain.ngo.services.CategoryService;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {


    private final CategoryService categoryService;
    private final JwtService jwtService;

    private final NgoRepository ngoRepository;

    public CategoryController(
            CategoryService categoryService,
            JwtService jwtService,
            NgoRepository ngoRepository
    ){
        this.categoryService = categoryService;
        this.jwtService = jwtService;
        this.ngoRepository = ngoRepository;
    }

    //______________Create a New Category------------
    @Operation(
            summary = "Create a new category",
            description = "This API allows an `NGO` user with `ACTIVE` status to create a new category.\n" +
                    "Requirements:\n" +
                    "- The user must be authenticated with a valid `JWT` token provided in the Authorization header.\n" +
                    "- The user must be associated with an `NGO` that has `ACTIVE` status.\n" +
                    "- Only users with the role 'NGO' can create categories.\n" +
                    "Process:\n" +
                    "- Validate the JWT token and extract the user ID.\n" +
                    "- Check that the NGO linked to the user ID is ACTIVE.\n" +
                    "- If valid, the category is created and returned in the response.\n" +
                    "Possible Errors:\n" +
                    "- `401` Unauthorized: JWT token is invalid or missing.\n" +
                    "- `403` Forbidden\n" +
                    "- `400` Bad Request: Category data is invalid or creation failed."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or category creation failed"),
            @ApiResponse(responseCode = "403", description = "Only NGO users can create categories"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token")
    })
    @PostMapping("/create")
    public ResponseEntity<CategoryApiResponseDto<Category>> createCategory(
            @RequestHeader("Authorization") String token,
            @RequestBody CategoryCreateDto categoryCreateDto
            ){
        try {

            System.out.println("Received DTO: " + categoryCreateDto);
            System.out.println("Category Name: " + (categoryCreateDto != null ? categoryCreateDto.getCategoryName() : "null"));

            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            // Check NGO Active
            ngoRepository.findByUser_IdAndUserStatus(userId, User.Status.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Active NGO not found for user id: " + userId));


            // Check if category already exists
            if (categoryService.existsByName(categoryCreateDto.getCategoryName())) {
                CategoryApiResponseDto<Category> response = new CategoryApiResponseDto<>();
                response.setSuccess(false);
                response.setMessage("Category with name '" + categoryCreateDto.getCategoryName() + "' already exists.");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }

            // Create Category
            Category category = Category.builder()
                    .categoryName(categoryCreateDto.getCategoryName())
                    .build();
            categoryService.createCategory(category);

            // Return JSON response
            CategoryApiResponseDto<Category> response = new CategoryApiResponseDto<>();
            response.setSuccess(true);
            response.setMessage("Category created successfully.");
            response.setData(category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CategoryApiResponseDto<Category> errorResponse = new CategoryApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setData(null);
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }

    //______________Get All Categories------------
    @Operation(
            summary = "Get all categories",
            description = "This API fetches a list of all categories stored in the system.\n" +
                    "Requirements:\n" +
                    "- The user must be authenticated with a valid JWT token.\n" +
                    "- The user must be associated with an `ACTIVE` NGO.\n" +
                    "- Only users linked with ACTIVE NGOs can access categories.\n" +
                    "Process:\n" +
                    "- Validate JWT token and extract the user ID.\n" +
                    "- Ensure that the NGO linked with the user ID is ACTIVE.\n" +
                    "- Fetch all categories from the database and return them.\n" +
                    "Possible Errors:\n" +
                    "- `401` Unauthorized: JWT token is invalid or missing.\n" +
                    "- `400` Bad Request: Failed to retrieve categories.\n" +
                    "- `403` Forbidden."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or retrieval failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token")
    })
    @GetMapping("/all")
    public ResponseEntity<CategoryApiResponseDto<List<Category>>> getAllCategories(
            @RequestHeader("Authorization") String token
    ) {
        try {
            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            // Check NGO Active
            ngoRepository.findByUser_IdAndUserStatus(userId, User.Status.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Active NGO not found for user id: " + userId));


            // Fetch all categories
            List<Category> categories = categoryService.getAllCategories();

            // Success message
            return ResponseEntity.ok(new CategoryApiResponseDto<>(true, "All categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CategoryApiResponseDto<>(false, e.getMessage(), null)
            );
        }
    }



    //______________Get Category By Id------------
    @Operation(
            summary = "Get category by ID",
            description = "This API retrieves a category by its unique UUID.\n" +
                    "Requirements:\n" +
                    "- The user must be authenticated with a valid `JWT` token.\n" +
                    "- The user must be associated with an `ACTIVE` `NGO`.\n" +
                    "- Only users linked with ACTIVE NGOs can access category details.\n" +
                    "Process:\n" +
                    "- Validate the JWT token and extract the user ID.\n" +
                    "- Ensure the NGO linked with the user ID is ACTIVE.\n" +
                    "- Look up the category by UUID and return it if found.\n" +
                    "Possible Errors:\n" +
                    "- `401` Unauthorized: JWT token is invalid or missing.\n" +
                    "- `404` Not Found: No category exists for the given ID.\n" +
                    "- `400` Bad Request: Invalid UUID or retrieval failed.\n" +
                    "- `403` Forbidden."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request or retrieval failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryApiResponseDto<Category>> getCategoryById(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") UUID categoryId
    ) {
        try {
            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            // Check NGO Active
            ngoRepository.findByUser_IdAndUserStatus(userId, User.Status.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Active NGO not found for user id: " + userId));

            // Fetch category by id
            return categoryService.getCategoryById(categoryId)
                    .map(category -> ResponseEntity.ok(
                            new CategoryApiResponseDto<>(true, "Category found", category)
                    ))
                    .orElseGet(() -> ResponseEntity.status(404).body(
                            new CategoryApiResponseDto<>(false, "Category not found", null)
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CategoryApiResponseDto<>(false, e.getMessage(), null)
            );
        }
    }


    // Search Categories
    @Operation(
            summary = "Search categories by keyword",
            description = "This API allows searching for `categories` using a keyword.\n" +
                    "Requirements:\n" +
                    "- The user must be authenticated with a valid JWT token.\n" +
                    "- The user must be associated with an ACTIVE NGO.\n" +
                    "Process:\n" +
                    "- Validate the JWT token and extract the user ID.\n" +
                    "- Ensure the NGO linked with the user ID is ACTIVE.\n" +
                    "- `Search` categories by keyword (partial or full match).\n" +
                    "- Return a list of matching categories.\n" +
                    "Possible Errors:\n" +
                    "- `401` Unauthorized: JWT token is invalid or missing.\n" +
                    "- `400` Bad Request: Retrieval failed or invalid keyword."+
                    "- `403` Forbidden."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or retrieval failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token")
    })
    @GetMapping("/search")
    public ResponseEntity<CategoryApiResponseDto<List<Category>>> searchCategories(
            @RequestHeader("Authorization") String token,
            @RequestParam("keyword") String keyword
    ) {
        try {
            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            // Check NGO Active
            ngoRepository.findByUser_IdAndUserStatus(userId, User.Status.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Active NGO not found for user id: " + userId));

            List<Category> results = categoryService.searchCategories(keyword);

            return ResponseEntity.ok(
                    new CategoryApiResponseDto<>(true, results.isEmpty() ? "No categories found" : "Categories retrieved successfully", results)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CategoryApiResponseDto<>(false, e.getMessage(), null)
            );
        }
    }



}
