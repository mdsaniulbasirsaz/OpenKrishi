package com.openkrishi.OpenKrishi.domain.ngo.controller;


import com.openkrishi.OpenKrishi.domain.auth.jwtServices.JwtService;
import com.openkrishi.OpenKrishi.domain.farmer.entity.Farmer;
import com.openkrishi.OpenKrishi.domain.farmer.repostitory.FarmerRepository;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.*;
import com.openkrishi.OpenKrishi.domain.ngo.entity.*;
import com.openkrishi.OpenKrishi.domain.ngo.repository.CategoryRepository;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import com.openkrishi.OpenKrishi.domain.ngo.services.DeliveryChargeService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {


    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final JwtService jwtService;
    private final NgoRepository ngoRepository;
    private final FarmerRepository farmerRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DeliveryChargeService deliveryChargeService;


    public ProductController(
            ProductService productService,
            JwtService jwtService,
            NgoRepository ngoRepository,
            FarmerRepository farmerRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            DeliveryChargeService deliveryChargeService
    ){
        this.productService = productService;
        this.jwtService = jwtService;
        this.ngoRepository = ngoRepository;
        this.farmerRepository = farmerRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.deliveryChargeService = deliveryChargeService;
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


    //-------------Update Product Availability--------------
    @Operation(
            summary = "Update product availability (available/unavailable)",
            description = """
        This API allows users to update the `availability` status of a product.

        Requirements:
        - The user must be authenticated with a valid JWT token.

        Process:
        - Validate the JWT token and extract user ID.
        - Verify that the user exists in the system.
        - Update the product availability based on the provided `isAvailable` parameter.

        Parameters:
        - `productId`: Unique identifier of the product.
        - `isAvailable`: Boolean value (true/false) to set product availability.

        Possible Errors:
        - `401` Unauthorized: JWT token invalid or missing.
        - `400` Bad Request: User not found or product not found.
        - `403` Forbidden: If the user does not have permission.
        - `500` Internal Server Error: Unexpected server error.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product availability updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or update failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{productId}/availability")
    public ResponseEntity<Map<String, String>>updateProductAvailability(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID productId,
            @RequestParam boolean isAvailable
    ) {
        try{
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));

            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            productService.updateProductAvailability(productId,isAvailable);


            return ResponseEntity.ok(Map.of(
                    "Status","Success",
                    "Message", isAvailable? "Product is Now Available." : "Product is Now Unavailable."
            ));
        }  catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("Status", "Error", "Message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("Status", "Error", "Message", "Internal Server Error", "Details", e.getMessage()));
        }
    }


    // Product Search BY Price (using Min and Max Price)
    @Operation(
            summary = "Search products within a price range",
            description = """
        This API allows users to search for `products` within a given `minPrice` and `maxPrice`.

        **Requirements:**
        - The user must be `authenticated` with a valid JWT token.

        **Process:**
        - Validate the `JWT` token and extract user ID.
        - Search for products where `localPrice` is between `minPrice` and `maxPrice`.

        **Parameters:**
        - `minPrice` (required): Minimum price filter.
        - `maxPrice` (required): Maximum price filter.

        **Possible Errors:**
        - `401` Unauthorized: JWT token invalid or missing.
        - `400` Bad Request: Invalid request or missing params.
        - `500` Internal Server Error: Any unexpected error.
        - `403` Forbidden: If the user does not have permission.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request or invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search/by-price")
    public ResponseEntity<?> searchProductsByPriceRange(
            @RequestHeader("Authorization") String token,
            @RequestParam double minPrice,
            @RequestParam double maxPrice
    ){
        try {
            UUID userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));


            List<ProductResponseDto> productResponse = productService.searchProductByPriceRange(minPrice, maxPrice)
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

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "Status", "Error",
                    "Message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "Status", "Error",
                    "Message", "Internal Server Error",
                    "Details", e.getMessage()
            ));
        }
    }

    // -----------Add Delivery Charge------------
    @Operation(
            summary = "Add delivery charge for a specific NGO",
            description = "This API allows admin or authorized users to add a delivery charge for a given `NGO`.\n" +
                    "Requirements:\n" +
                    "- The user must be authenticated with a valid `JWT` token.\n" +
                    "- The `ngoId` must be valid and exist in the database.\n" +
                    "Process:\n" +
                    "- Validate the `JWT` token and check user permission.\n" +
                    "- Validate the `amountPerKm` value.\n" +
                    "- Save the delivery charge linked to the specified `NGO`.\n" +
                    "Parameters:\n" +
                    "- `ngoId` (required): UUID of the NGO.\n" +
                    "- `amountPerKm` (required): Delivery charge per kilometer.\n" +
                    "Possible Errors:\n" +
                    "- `401` Unauthorized: JWT token invalid or missing.\n" +
                    "- `400` Bad Request: Invalid `ngoId` or `amountPerKm` value.\n" +
                    "- `500` Internal Server Error: Unexpected errors during saving."
    )

    @PostMapping("/delivery/charge/create")
    public ResponseEntity<?> addDeliveryCharge(@RequestBody DeliveryChargeRequestDto request) {
        DeliveryCharge savedCharge = deliveryChargeService.addDeliveryCharge(
                request.getNgoId(),
                request.getAmountPerKm()
        );
        DeliveryChargeResponseDto response = new DeliveryChargeResponseDto(
                savedCharge.getId(),
                savedCharge.getAmountPerKm(),
                savedCharge.getNgo().getNgoId()
        );

        return ResponseEntity.ok(response);
    }



    //------------------Product Order--------------------
    @Operation(
            summary = "Create a new order with multiple products",
            description = "This API allows users to create a new `order` containing one or more products.\n" +
                    "Requirements:\n" +
                    "- The user must be `authenticated` with a valid JWT token.\n" +
                    "- The `userId` and `ngoId` must be valid UUIDs.\n" +
                    "Process:\n" +
                    "- Validate the `JWT token` and extract `user ID`.\n" +
                    "- Validate items list to ensure each product exists and quantity is valid.\n" +
                    "- Calculate total price and delivery charges.\n" +
                    "- Save the order and associated order items in the database.\n" +
                    "Parameters:\n" +
                    "- `userId` (required): UUID of the user placing the order.\n" +
                    "- `ngoId` (required): UUID of the NGO fulfilling the order.\n" +
                    "- `items` (required): List of OrderItemRequestDto containing productId, quantity, and optional description.\n" +
                    "Possible Errors:\n" +
                    "- `401` Unauthorized: JWT token invalid or missing.\n" +
                    "- `400` Bad Request: Invalid UUIDs, empty items list, or invalid quantities.\n" +
                    "- `500` Internal Server Error: Unexpected errors during order creation.\n" +
                    "- `403` Forbidden: If the user does not have permission to create orders."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully with success message"),
            @ApiResponse(responseCode = "400", description = "Bad request or invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })


    @PostMapping("/order/create")
    public ResponseEntity<OrderSuccessResponseDto> createOrder(
            @RequestParam UUID userId,
            @RequestParam UUID ngoId,
            @RequestBody List<OrderItemRequestDto> items
    ) {
        //Create Order
        productService.createOrder(userId, ngoId, items);



        return ResponseEntity.ok(
                new OrderSuccessResponseDto("Success", "Order Created Successfully.")
        );

    }
}
