package com.openkrishi.OpenKrishi.domain.customer.repository;

import com.openkrishi.OpenKrishi.domain.customer.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    // Find all subscriptions for a specific customer by customer ID
    @Query("""
        SELECT s
        FROM Subscription s
        JOIN s.customers c
        WHERE c.id = :customerId
        """)
    List<Subscription> findByCustomerId(@Param("customerId") UUID customerId);

    // Find subscriptions for a specific customer by customer ID and plan name
    @Query("""
        SELECT s
        FROM Subscription s
        JOIN s.customers c
        WHERE c.id = :customerId
          AND s.planName = :planName
        """)
    List<Subscription> findByCustomerIdAndPlanName(
            @Param("customerId") UUID customerId,
            @Param("planName") String planName);

    // Find a subscription by subscription ID and customer ID
    @Query("""
        SELECT s
        FROM Subscription s
        JOIN s.customers c
        WHERE s.id = :subscriptionId
          AND c.id = :customerId
        """)
    Optional<Subscription> findByIdAndCustomerId(
            @Param("subscriptionId") UUID subscriptionId,
            @Param("customerId") UUID customerId);

    // Find all subscriptions by plan name
    List<Subscription> findByPlanName(String planName);
}
