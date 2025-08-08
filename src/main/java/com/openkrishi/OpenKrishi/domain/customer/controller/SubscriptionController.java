package com.openkrishi.OpenKrishi.domain.customer.controller;

import com.openkrishi.OpenKrishi.domain.customer.dtos.SubscriptionCreateDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.SubscriptionResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.services.SubscriptionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // Create a new subscription Ngo or Admin
    @PostMapping("/ngo/create")
    public ResponseEntity<SubscriptionResponseDto> createSubscription(@Valid @RequestBody SubscriptionCreateDto dto, Authentication authentication) {

        String email = authentication.getName();

        SubscriptionResponseDto response = subscriptionService.createSubscription(dto,email);
        return ResponseEntity.ok(response);
    }

    // Get subscription by id
    @GetMapping("/all/plan")
        public ResponseEntity<List<SubscriptionResponseDto>> getAllSubscriptions() {
            List<SubscriptionResponseDto> subscriptions = subscriptionService.getAllSubscriptionPlans();
            return ResponseEntity.ok(subscriptions);
    }

    // Get all subscriptions for a specific customer
    @GetMapping("/my/plan/{planName}")
    public ResponseEntity<List<SubscriptionResponseDto>> getMySubscriptionsByPlan(@PathVariable String planName, Authentication authentication) {
        String email = authentication.getName();
        List<SubscriptionResponseDto> subscriptions = subscriptionService.getSubscriptionsByEmailAndPlan(email, planName);
        return ResponseEntity.ok(subscriptions);
    }


}
