package com.openkrishi.OpenKrishi.domain.ngo.services;


import com.openkrishi.OpenKrishi.domain.ngo.entity.DeliveryCharge;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import com.openkrishi.OpenKrishi.domain.ngo.repository.DeliveryChargeRepository;
import com.openkrishi.OpenKrishi.domain.ngo.repository.NgoRepository;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryChargeService {


    private final DeliveryChargeRepository deliveryChargeRepository;
    private final NgoRepository ngoRepository;

    public BigDecimal calculateDeliveryCharge (User ngoUser, double distanceKm) {
        // Step 1: Fetch NGO by User
        Ngo ngo = ngoRepository.findByUser(ngoUser)
                .orElseThrow(() -> new RuntimeException("NGO not found for user"));

        // Step 2: Fetch delivery charge by NGO ID
        DeliveryCharge deliveryCharge = deliveryChargeRepository.findByNgo_NgoId(ngo.getNgoId())
                .orElseThrow(() -> new RuntimeException("Delivery charge not set for NGO"));

        return BigDecimal.valueOf(distanceKm)
                .multiply(BigDecimal.valueOf(deliveryCharge.getAmountPerKm()));
    }

    public DeliveryCharge addDeliveryCharge(UUID ngoId, double amountPerKm) {
        Ngo ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        DeliveryCharge deliveryCharge = DeliveryCharge.builder()
                .amountPerKm(amountPerKm)
                .ngo(ngo) // set relationship
                .build();

        ngo.getDeliveryCharges().add(deliveryCharge);

        return deliveryChargeRepository.save(deliveryCharge);
    }



    // Haversine formula
    public static double calculateDistanceInKm(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
