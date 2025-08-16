package com.openkrishi.OpenKrishi.domain.ngo.repository;

import com.openkrishi.OpenKrishi.domain.customer.entity.Customer;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NgoRepository extends JpaRepository<Ngo, UUID> {
    Optional<Ngo> findByUser_Id(UUID userId);
    boolean existsByUser_Id(UUID userId);
    Optional<Ngo> findByUser_Email(String email);
}
