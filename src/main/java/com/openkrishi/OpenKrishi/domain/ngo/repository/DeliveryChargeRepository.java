package com.openkrishi.OpenKrishi.domain.ngo.repository;

import com.openkrishi.OpenKrishi.domain.ngo.entity.DeliveryCharge;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryChargeRepository extends JpaRepository<DeliveryCharge, UUID> {
    Optional<DeliveryCharge> findByNgo(Ngo ngo);
    Optional<DeliveryCharge> findByNgo_NgoId(UUID ngoId);


}
