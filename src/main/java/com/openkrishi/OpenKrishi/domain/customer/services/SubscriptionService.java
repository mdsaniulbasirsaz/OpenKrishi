package com.openkrishi.OpenKrishi.domain.customer.services;

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

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + email));

        Subscription subscription = subscriptionMapper.toEntity(dto, customer, email);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toDto(savedSubscription);
    }

    // Get all subscriptions for all customer
    public List<SubscriptionResponseDto> getAllSubscriptionPlans() {

        List<Subscription> subscriptions = subscriptionRepository.findAll();
        return subscriptions.stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }


    // Get all subscriptions by plan name
    public List<SubscriptionResponseDto> getSubscriptionsByEmailAndPlan(String email, String planName) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + email));

        List<Subscription> subscriptions = subscriptionRepository.findByCustomerIdAndPlanName(customer.getId(), planName);
        return subscriptions.stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    // Get all subscriptions by plan name
    public java.util.Optional<SubscriptionResponseDto> getSubscriptionByIdAndEmail(UUID subscriptionId, String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + email));

        return subscriptionRepository.findByIdAndCustomerId(subscriptionId, customer.getId())
                .map(subscriptionMapper::toDto);
    }

}
