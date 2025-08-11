package com.openkrishi.OpenKrishi.domain.customer.services;

import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.SubscriptionCreateDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.SubscriptionResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.entity.Customer;
import com.openkrishi.OpenKrishi.domain.customer.entity.Subscription;
import com.openkrishi.OpenKrishi.domain.customer.mapper.SubscriptionMapper;
import com.openkrishi.OpenKrishi.domain.customer.repository.CustomerRepository;
import com.openkrishi.OpenKrishi.domain.customer.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final SubscriptionMapper subscriptionMapper;


    // Create a new subscription 
    public SubscriptionResponseDto createSubscription(SubscriptionCreateDto dto, String email) {

        // Find the customer (typically the NGO or Admin creating the subscription) by email
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + email));

        // Map DTO to Subscription entity
        Subscription subscription = subscriptionMapper.toEntity(dto, customer, email);

        // Save subscription to database
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Map saved entity to response DTO and return
        return subscriptionMapper.toDto(savedSubscription);
    }

    // Retrieve all subscription plans available
    public List<SubscriptionResponseDto> getAllSubscriptionPlans() {

        List<Subscription> subscriptions = subscriptionRepository.findAll();
        return subscriptions.stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }


    // Retrieve subscriptions for a specific customer filtered by plan name
    public List<SubscriptionResponseDto> getSubscriptionsByEmailAndPlan(String email, String planName) {
        // Find customer by email
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + email));
        // Find subscriptions by customer ID and plan name
        List<Subscription> subscriptions = subscriptionRepository.findByCustomerIdAndPlanName(customer.getId(), planName);
        // Map to DTO list and return
        return subscriptions.stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    // Retrieve all subscriptions for a specific customer by email
    public List<SubscriptionResponseDto> getSubscriptionsByEmail(String email) {
        // Find customer by email
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + email));

        // Find subscriptions by customer ID
        List<Subscription> subscriptions = subscriptionRepository.findByCustomerId(customer.getId());

        return subscriptions.stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    // Add a customer to an existing subscription plan
    public void subscribeCustomerToSubscription(UUID subscriptionId, String customerEmail) {
        Customer customer = customerRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + customerEmail));

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));

        if (!subscription.getCustomers().contains(customer)) {
            subscription.getCustomers().add(customer);
            subscriptionRepository.save(subscription);
        }
    }

    // Get subscription by ID
    public SubscriptionResponseDto getSubscriptionById(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));
        return subscriptionMapper.toDto(subscription);  // Mapper used here
    }

    // Unsubscribe customer from subscription
    public void unsubscribeCustomerFromSubscription(UUID subscriptionId, String customerEmail) {
        Customer customer = customerRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + customerEmail));
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));
        if (subscription.getCustomers().contains(customer)) {
            subscription.getCustomers().remove(customer);
            subscriptionRepository.save(subscription);
        }
    }

    // Update subscription plan
    public SubscriptionResponseDto updateSubscription(UUID subscriptionId, SubscriptionCreateDto dto, String email) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));
        // Update fields directly
        subscription.setPlanName(dto.getPlanName());
        subscription.setPrice(dto.getPrice());
        subscription.setStartDate(dto.getStartDate());
        subscription.setEndDate(dto.getEndDate());
        subscription.setNgoShare(dto.getNgoShare());
        subscription.setUpdatedAt(LocalDateTime.now());
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDto(updatedSubscription);  // Mapper used here
    }

    // Get customers subscribed to a subscription
    @Transactional
    public List<CustomerResponseDto> getCustomersBySubscriptionId(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));
        return subscription.getCustomers().stream()
                .map(customer -> CustomerResponseDto.builder()
                        .id(customer.getId())
                        .fullName(customer.getFullName())
                        .email(customer.getEmail())
                        .phone(customer.getPhone())
                        .latitude(customer.getLatitude())
                        .longitude(customer.getLongitude())
                        .createdAt(customer.getCreatedAt())
                        .updatedAt(customer.getUpdatedAt())
                        .subscriptions(
                                customer.getSubscriptions() != null
                                        ? customer.getSubscriptions().stream()
                                        .map(subscriptionMapper::toDto)
                                        .collect(Collectors.toList())
                                        : Collections.emptyList()
                        )
                        .build())
                .collect(Collectors.toList());
    }

    // Get active subscriptions for a customer
    public List<SubscriptionResponseDto> getActiveSubscriptionsByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + email));
        List<Subscription> subscriptions = subscriptionRepository.findByCustomerId(customer.getId());
        LocalDateTime now = LocalDateTime.now();

        List<Subscription> activeSubs = subscriptions.stream()
                .filter(s -> s.getStartDate().isBefore(now) && s.getEndDate().isAfter(now))
                .toList();

        return activeSubs.stream()
                .map(subscriptionMapper::toDto)
                .toList();
    }

    // Extend subscription by extra days
    public void extendSubscription(UUID subscriptionId, int extraDays, String email) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));
        subscription.setEndDate(subscription.getEndDate().plusDays(extraDays));
        subscription.setUpdatedAt(LocalDateTime.now());
        subscriptionRepository.save(subscription);
    }

    // Delete subscription plan
    public void deleteSubscription(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));
        subscriptionRepository.delete(subscription);
    }



}
