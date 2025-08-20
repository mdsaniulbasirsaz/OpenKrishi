package com.openkrishi.OpenKrishi.domain.ngo.repository;

import com.openkrishi.OpenKrishi.domain.ngo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

}
