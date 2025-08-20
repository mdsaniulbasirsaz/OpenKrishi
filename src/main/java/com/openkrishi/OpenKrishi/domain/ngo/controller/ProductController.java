package com.openkrishi.OpenKrishi.domain.ngo.controller;


import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.farmer.entity.Farmer;
import com.openkrishi.OpenKrishi.domain.farmer.repostitory.FarmerRepository;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.ProductCreateDto;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.ProductResponseDto;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.ProductUpdateDto;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Category;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Product;
import com.openkrishi.OpenKrishi.domain.ngo.repository.CategoryRepository;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import com.openkrishi.OpenKrishi.domain.ngo.services.ProductService;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import io.jsonwebtoken.io.IOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {


    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final JwtService jwtService;
    private final NgoRepository ngoRepository;
    private final FarmerRepository farmerRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    public ProductController(
            ProductService productService,
            JwtService jwtService,
            NgoRepository ngoRepository,
            FarmerRepository farmerRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository
    ){
        this.productService = productService;
        this.jwtService = jwtService;
        this.ngoRepository = ngoRepository;
        this.farmerRepository = farmerRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }


    //---------------------Create Product-------------------
    @Operation(
            summary = "Create a new product",
            description = "This API allows creating a new `product` linked to a Farmer, Category, and NGO.\n" +
                    "Requirements:\n" +
                    "- The user must be authenticated with a valid JWT token.\n" +
                    "- The user must be associated with an existing NGO.\n" +
                    "Process:\n" +
                    "- Validate the JWT token and extract the user ID.\n" +
                    "- Retrieve the user, NGO, Farmer, and Category.\n" +
                    "- Create a new product using the provided details.\n" +
                    "- Save the product in the database.\n" +
                    "Possible Errors:\n" +
                    "- `401` Unauthorized: JWT token is invalid or missing.\n" +
                    "- `400` Bad Request: Validation failed or product creation failed.\n" +
                    "- `404` Not Found: User, NGO, Farmer, or Category not found." +
                    "- `403` Forbidden."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Related entities not found (User, NGO, Farmer, Category)"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(
            @RequestHeader("Authorization") String token,
            @RequestBody ProductCreateDto dto
    ) {
        try {
            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Ngo ngo = ngoRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Ngo not found"));
            Farmer farmer = farmerRepository.findById(dto.getFarmerId())
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            Product product = Product.builder()
                    .productName(dto.getProductName())
                    .value(dto.getValue())
                    .unit(dto.getUnit())
                    .season(dto.getSeason())
                    .description(dto.getDescription())
                    .localPrice(dto.getLocalPrice())
                    .marketPrice(dto.getMarketPrice())
                    .discount(dto.getDiscount())
                    .isAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true)
                    .category(category)
                    .farmer(farmer)
                    .ngo(ngo)
                    .build();

            productService.createProduct(product);
            return ResponseEntity.ok("Product Added Successfully.");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ---------------- Image Upload for Existing Product ----------------
    @Operation(
            summary = "Upload image for existing product",
            description = "This API allows uploading and attaching an image to an existing `product`.\n" +
                    "Requirements:\n" +
                    "- The user must be authenticated with a valid JWT token.\n" +
                    "- The user must be associated with an existing NGO.\n" +
                    "Process:\n" +
                    "- Validate the JWT token and extract the user ID.\n" +
                    "- Retrieve the user and linked NGO.\n" +
                    "- Upload and attach the image to the product.\n" +
                    "Possible Errors:\n" +
                    "- `401` Unauthorized: JWT token is invalid or missing.\n" +
                    "- `400` Bad Request: Invalid product ID or upload failed.\n" +
                    "- `500` Internal Server Error: Image upload error." +
                    "- `403` Forbidden."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or invalid product ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token"),
            @ApiResponse(responseCode = "500", description = "Internal server error while uploading image"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @PostMapping("/upload-image/{productId}")
    public ResponseEntity<?> uploadProductImage(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID productId,
            @RequestParam("imageFile") MultipartFile imageFile
    ) {
        try {
            // JWT validation
            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Ngo ngo = ngoRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Ngo not found"));

            productService.attachImageToProduct(productId, imageFile);
            return ResponseEntity.ok("Product Image Added Successfully.");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image upload error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    // ---------------- Fetch Product by ID ----------------
    @Operation(
            summary = "Fetch product by ID",
            description = "This API allows fetching details of a single `product` by `productId`.\n" +
                    "Requirements:\n" +
                    "- The user must be authenticated with a valid JWT token.\n" +
                    "Process:\n" +
                    "- Validate the JWT token and extract the user ID.\n" +
                    "- Retrieve the user.\n" +
                    "- Fetch the product and map it to ProductResponseDto.\n" +
                    "Possible Errors:\n" +
                    "- `401` Unauthorized: JWT token is invalid or missing.\n" +
                    "- `400` Bad Request: Invalid request.\n" +
                    "- `404` Not Found: Product not found." +
                    "- `403` Forbidden."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID productId
    ) {
        try {
            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return productService.getProductById(productId)
                    .map(product -> {
                        ProductResponseDto dto = new ProductResponseDto(
                                product.getId(),
                                product.getProductName(),
                                product.getValue(),
                                product.getUnit(),
                                product.getSeason(),
                                product.getDescription(),
                                product.getLocalPrice(),
                                product.getMarketPrice(),
                                product.getDiscount(),
                                product.getIsAvailable(),
                                product.getProductImage(),
                                product.getCategory() != null ? product.getCategory().getCategoryName() : null,
                                product.getFarmer() != null ? product.getFarmer().getFarmerName() : null,
                                product.getNgo() != null ? product.getNgo().getManagerName() : null
                        );
                        return ResponseEntity.ok(dto);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    // ---------------- Fetch All Products ----------------
    @Operation(
            summary = "Fetch all products",
            description = "This API allows fetching a list of all `products`.\n" +
                    "Requirements:\n" +
                    "- The user must be authenticated with a valid JWT token.\n" +
                    "Process:\n" +
                    "- Validate the JWT token and extract the user ID.\n" +
                    "- Retrieve the user.\n" +
                    "- Fetch all products and map them to ProductResponseDto.\n" +
                    "Possible Errors:\n" +
                    "- `401` Unauthorized: JWT token is invalid or missing.\n" +
                    "- `400` Bad Request: Retrieval failed or invalid request." +
                    "- `403` Forbidden."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or retrieval failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Fetch all products
            List<ProductResponseDto> dtos = productService.getAllProducts()
                    .stream()
                    .map(product -> new ProductResponseDto(
                            product.getId(),
                            product.getProductName(),
                            product.getValue(),
                            product.getUnit(),
                            product.getSeason(),
                            product.getDescription(),
                            product.getLocalPrice(),
                            product.getMarketPrice(),
                            product.getDiscount(),
                            product.getIsAvailable(),
                            product.getProductImage(),
                            product.getCategory() != null ? product.getCategory().getCategoryName() : null,
                            product.getFarmer() != null ? product.getFarmer().getFarmerName() : null,
                            product.getNgo() != null ? product.getNgo().getManagerName() : null
                    ))
                    .toList();

            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // -------------Product Update By Id-------------------
    @Operation(
            summary = "Update product details",
            description = """
            This API is used to update an existing product by its ID.
            
            **Requirements:**
            - The user must be authenticated with a valid **JWT token**.
            - The product ID must exist in the database.
            - Only specific fields can be updated:
              - `description`
              - `localPrice`
              - `marketPrice`
              - `discount`
              - `value`
            
            **Process:**
            - Validate the JWT token and extract the user ID.
            - Ensure the user exists in the system.
            - Find the product by ID.
            - Update only the provided fields (others remain unchanged).
            - Save the updated product in the database.
            
            **Possible Errors:**
            - `401` Unauthorized: If JWT token is invalid or missing.
            - `400` Bad Request: If product ID does not exist or payload is invalid.
            - `403` Forbidden: If the user does not have permission.
            """
    )

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or update failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") UUID productId,
            @RequestBody ProductUpdateDto productUpdateDto
    ) {
        try{
            String jwt = token.replace("Bearer ","");
            UUID userId = jwtService.extractUserId(jwt);


            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User Not Found."));

            // Product Update
            productService.updateProduct(productId, productUpdateDto);

            return ResponseEntity.ok("Product Update SuccessFully.");

        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Error Updating Product: "+ e.getMessage());
        }
    }


    //--------------Product Search------------------
    @Operation(
            summary = "Search products by keyword",
            description = """
        This API allows users to `search` for `products` using a `keyword`.

        **Requirements:**
        - The user must be `authenticated` with a valid JWT token.

        **Process:**
        - Validate the `JWT` token and extract user ID.
        - Search products where name or description contains the keyword (case-insensitive).

        **Possible Errors:**
        - `401` Unauthorized: JWT token invalid or missing.
        - `400` Bad Request: Any other invalid request.
        - `403` Forbidden: If the user does not have permission.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or retrieval failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token")
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestHeader("Authorization") String token,
            @RequestParam("keyword") String keyword
    ) {
        try {
            String jwt = token.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(jwt);

            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<ProductResponseDto> productResponse = productService.searchProductsByKeyword(keyword)
                    .stream()
                    .map(product -> new ProductResponseDto(
                            product.getId(),
                            product.getProductName(),
                            product.getValue(),
                            product.getUnit(),
                            product.getSeason(),
                            product.getDescription(),
                            product.getLocalPrice(),
                            product.getMarketPrice(),
                            product.getDiscount(),
                            product.getIsAvailable(),
                            product.getProductImage(),
                            product.getCategory() != null ? product.getCategory().getCategoryName() : null,
                            product.getFarmer() != null ? product.getFarmer().getFarmerName() : null,
                            product.getNgo() != null ? product.getNgo().getManagerName() : null
                    ))
                    .toList();

            return ResponseEntity.ok(productResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error searching products: " + e.getMessage());
        }
    }



}
