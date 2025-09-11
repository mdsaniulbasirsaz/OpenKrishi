package com.openkrishi.OpenKrishi.domain.ngo.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.openkrishi.OpenKrishi.domain.ngo.dtos.ProductUpdateDto;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Product;
import com.openkrishi.OpenKrishi.domain.ngo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;


    public ProductService(
            ProductRepository productRepository,
            Cloudinary cloudinary
    ) {
        this.productRepository = productRepository;
        this.cloudinary = cloudinary;
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
}
