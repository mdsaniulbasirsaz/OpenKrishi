package com.openkrishi.OpenKrishi.domain.customer.repository;

import com.openkrishi.OpenKrishi.domain.customer.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;


public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {


    List<Subscription> findByCustomerId(UUID customerId);

    List<Subscription> findByCustomerIdAndPlanName(UUID customerId, String planName);

    Optional<Subscription> findByIdAndCustomerId(UUID id, UUID customerId);

    List<Subscription> findByPlanName(String planName);
}
