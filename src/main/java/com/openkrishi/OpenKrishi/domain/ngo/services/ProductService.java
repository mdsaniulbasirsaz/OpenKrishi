package com.openkrishi.OpenKrishi.domain.ngo.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.OrderItemRequestDto;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.ProductUpdateDto;
import com.openkrishi.OpenKrishi.domain.ngo.entity.*;
import com.openkrishi.OpenKrishi.domain.ngo.repository.DeliveryChargeRepository;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import com.openkrishi.OpenKrishi.domain.ngo.repository.OrderRepository;
import com.openkrishi.OpenKrishi.domain.ngo.repository.ProductRepository;
import com.openkrishi.OpenKrishi.domain.user.entity.Address;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import com.openkrishi.OpenKrishi.domain.user.repository.AddressRepository;
import com.openkrishi.OpenKrishi.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;
    private final UserRepository userRepository;
    private final NgoRepository ngoRepository;
    private final AddressRepository addressRepository;
    private final DeliveryChargeRepository deliveryChargeRepository;
    private final OrderRepository orderRepository;
    private final DeliveryChargeService deliveryChargeService;


    public ProductService(
            ProductRepository productRepository,
            Cloudinary cloudinary,
            UserRepository userRepository,
            NgoRepository ngoRepository,
            AddressRepository addressRepository,
            DeliveryChargeRepository deliveryChargeRepository,
            OrderRepository orderRepository,
            DeliveryChargeService deliveryChargeService
    ) {
        this.productRepository = productRepository;
        this.cloudinary = cloudinary;
        this.userRepository = userRepository;
        this.ngoRepository = ngoRepository;
        this.addressRepository = addressRepository;
        this.deliveryChargeRepository = deliveryChargeRepository;
        this.orderRepository = orderRepository;
        this.deliveryChargeService = deliveryChargeService;
    }


    //------------Create Product without Image--------------
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }


    // ---------------- Upload image to Cloudinary ----------------
    public String uploadImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Product image must be provided.");
        }

        @SuppressWarnings("unchecked")
        var uploadResult = (java.util.Map<String, Object>) cloudinary.uploader()
                .upload(imageFile.getBytes(), ObjectUtils.emptyMap());

        return uploadResult.get("secure_url").toString();
    }


    // ---------------- Attach image to existing product ----------------
    public Product attachImageToProduct(UUID productId, MultipartFile imageFile) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found for ID: " + productId));

        String imageUrl = uploadImage(imageFile);
        product.setProductImage(imageUrl);

        return productRepository.save(product);
    }

    // ---------------- Fetch Product by ID ----------------
    public Optional<Product> getProductById(UUID productId) {

        return productRepository.findById(productId);
    }


    // ---------------- Get All Products ----------------
    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }


    //---------------Update Product----------------
    public Product updateProduct(UUID productId, ProductUpdateDto productUpdateDto)
    {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not Found."));

        if(productUpdateDto.getDescription() !=null)
        {
            product.setDescription(productUpdateDto.getDescription());
        }

        if(productUpdateDto.getLocalPrice() !=null)
        {
            product.setLocalPrice(productUpdateDto.getLocalPrice());
        }

        if(productUpdateDto.getMarketPrice() !=null)
        {
            product.setMarketPrice(productUpdateDto.getMarketPrice());
        }

        if(productUpdateDto.getDiscount()  !=null)
        {
            product.setDiscount(productUpdateDto.getDiscount());
        }

        if(productUpdateDto.getValue() !=null)
        {
            product.setValue(productUpdateDto.getValue());
        }

        return productRepository.save(product);


    }

    //------------Product Search------------
    public List<Product> searchProductsByKeyword(String keyword)
    {
        if(keyword == null || keyword.isEmpty())
        {
            return productRepository.findAll();
        }
        return productRepository.findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }


    // ---------------- Update Product Availability ----------------
    public void updateProductAvailability(UUID productId, boolean isAvailable)
    {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product Not Found."));

        // Set Availability
        product.setIsAvailable(isAvailable);
        productRepository.save(product);
    }

    //-------------Search Products by Price Range-----------------
    public List<Product> searchProductByPriceRange(double minPrice, double maxPrice)
    {
        return productRepository.findByLocalPriceBetween(minPrice, maxPrice);
    }

    // ----------------- Order Methods -----------------

    @Transactional
    public Order createOrder(UUID userId, UUID ngoId, List<OrderItemRequestDto> items) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Ngo ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));



        // Fetch address linked to user
        Address address = addressRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Address not found for user with id: " + userId));

        // Distance calculation using Haversine
        double distanceKm = DeliveryChargeService.calculateDistanceInKm(
                user.getLatitude(), user.getLongitude(), ngo.getUser().getLatitude(), ngo.getUser().getLongitude()
        );

        BigDecimal deliveryCost = deliveryChargeService.calculateDeliveryCharge(ngo.getUser(), distanceKm);


        List<OrderItem> orderItems = items.stream().map(itemRequest -> {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));
            return OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        BigDecimal productTotal = orderItems.stream()
                .map(oi -> oi.getProduct().getLocalPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPrice = productTotal.add(deliveryCost);

        Order order = Order.builder()
                .user(user)
                .ngo(ngo)
                .address(address)
                .orderItems(orderItems)
                .deliveryCharge(deliveryCost)
                .totalPrice(totalPrice)
                .build();

        orderItems.forEach(oi -> oi.setOrder(order));

        return orderRepository.save(order);
    }
}

