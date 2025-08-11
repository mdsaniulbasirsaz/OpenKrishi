package com.openkrishi.OpenKrishi.domain.customer.controller;

import com.openkrishi.OpenKrishi.domain.customer.dtos.CustomerResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.SubscriptionCreateDto;
import com.openkrishi.OpenKrishi.domain.customer.dtos.SubscriptionResponseDto;
import com.openkrishi.OpenKrishi.domain.customer.services.SubscriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/v1/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // Create a new subscription Ngo or Admin
    @Operation(summary = "Create a new subscription plan",
            description = "Allows an NGO or Admin to create a new subscription plan",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subscription created successfully",
                            content = @Content(schema = @Schema(implementation = SubscriptionResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    @PostMapping("/ngo/create")
    public ResponseEntity<SubscriptionResponseDto> createSubscription(@Valid @RequestBody SubscriptionCreateDto dto, Authentication authentication) {

        String email = authentication.getName();

        SubscriptionResponseDto response = subscriptionService.createSubscription(dto,email);
        return ResponseEntity.ok(response);
    }

    // Get subscription by id
    @Operation(summary = "Get all subscription plans",
            description = "Retrieve all available subscription plans",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of subscriptions retrieved",
                            content = @Content(schema = @Schema(implementation = SubscriptionResponseDto.class)))
            })
    @GetMapping("/all/plan")
        public ResponseEntity<List<SubscriptionResponseDto>> getAllSubscriptions() {
            List<SubscriptionResponseDto> subscriptions = subscriptionService.getAllSubscriptionPlans();
            return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "Get subscription details by ID",
            description = "Fetch detailed information of a subscription plan by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subscription found",
                            content = @Content(schema = @Schema(implementation = SubscriptionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Subscription not found")
            })
    @GetMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionResponseDto> getSubscriptionById(
            @Parameter(description = "Subscription ID") @PathVariable UUID subscriptionId) {
        SubscriptionResponseDto dto = subscriptionService.getSubscriptionById(subscriptionId);
        return ResponseEntity.ok(dto);
    }

    // Get all subscriptions for a specific customer
    @Operation(summary = "Get subscriptions for logged-in customer",
            description = "Retrieve all subscriptions associated with the authenticated customer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of customer subscriptions retrieved",
                            content = @Content(schema = @Schema(implementation = SubscriptionResponseDto.class)))
            })
    @GetMapping("/my/subscriptions")
    public ResponseEntity<List<SubscriptionResponseDto>> getMySubscriptions(Authentication authentication) {
        String email = authentication.getName();
        List<SubscriptionResponseDto> subscriptions = subscriptionService.getSubscriptionsByEmail(email);
        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "Subscribe authenticated customer to a subscription plan",
            description = "Allows the logged-in customer to subscribe to a specific subscription plan by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subscribed successfully"),
                    @ApiResponse(responseCode = "404", description = "Subscription or customer not found")
            })
    @PostMapping("/{subscriptionId}/subscribe")
    public ResponseEntity<String> subscribeToSubscription(@PathVariable UUID subscriptionId, Authentication authentication) {
        String email = authentication.getName();
        subscriptionService.subscribeCustomerToSubscription(subscriptionId, email);
        return ResponseEntity.ok("Subscribed successfully");
    }


    @Operation(summary = "Unsubscribe from a subscription",
            description = "Allow the logged-in customer to unsubscribe from a specific subscription plan",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unsubscribed successfully"),
                    @ApiResponse(responseCode = "404", description = "Subscription or customer not found")
            })
    @DeleteMapping("/{subscriptionId}/unsubscribe")
    public ResponseEntity<String> unsubscribeFromSubscription(@PathVariable UUID subscriptionId, Authentication authentication) {
        String email = authentication.getName();
        subscriptionService.unsubscribeCustomerFromSubscription(subscriptionId, email);
        return ResponseEntity.ok("Unsubscribed successfully");
    }

    @Operation(summary = "Update subscription plan",
            description = "Allows an NGO or Admin to update an existing subscription plan",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subscription updated successfully",
                            content = @Content(schema = @Schema(implementation = SubscriptionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Subscription not found")
            })
    @PutMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionResponseDto> updateSubscription(
            @PathVariable UUID subscriptionId,
            @Valid @RequestBody SubscriptionCreateDto dto,
            Authentication authentication) {
        String email = authentication.getName();
        SubscriptionResponseDto updated = subscriptionService.updateSubscription(subscriptionId, dto, email);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get customers subscribed to a subscription plan",
            description = "Retrieve list of customers who have subscribed to a given subscription plan",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of customers retrieved",
                            content = @Content(schema = @Schema(implementation = CustomerResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Subscription not found")
            })
    @GetMapping("/{subscriptionId}/customers")
    public ResponseEntity<List<CustomerResponseDto>> getCustomersBySubscription(@PathVariable UUID subscriptionId) {
        List<CustomerResponseDto> customers = subscriptionService.getCustomersBySubscriptionId(subscriptionId);
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Get active subscriptions for logged-in customer",
            description = "Retrieve all active subscription plans for the authenticated customer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Active subscriptions retrieved",
                            content = @Content(schema = @Schema(implementation = SubscriptionResponseDto.class)))
            })
    @GetMapping("/my/active")
    public ResponseEntity<List<SubscriptionResponseDto>> getActiveSubscriptions(Authentication authentication) {
        String email = authentication.getName();
        List<SubscriptionResponseDto> activeSubscriptions = subscriptionService.getActiveSubscriptionsByEmail(email);
        return ResponseEntity.ok(activeSubscriptions);
    }

    @Operation(summary = "Extend subscription duration",
            description = "Allow a customer to extend or renew a subscription plan",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subscription extended successfully"),
                    @ApiResponse(responseCode = "404", description = "Subscription not found")
            })
    @PostMapping("/{subscriptionId}/extend")
    public ResponseEntity<String> extendSubscription(
            @PathVariable UUID subscriptionId,
            @RequestParam int extraDays,
            Authentication authentication) {
        String email = authentication.getName();
        subscriptionService.extendSubscription(subscriptionId, extraDays, email);
        return ResponseEntity.ok("Subscription extended successfully");
    }

    @Operation(summary = "Delete a subscription plan",
            description = "Allows an Admin or NGO to delete a subscription plan by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subscription deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Subscription not found")
            })
    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<String> deleteSubscription(@PathVariable UUID subscriptionId) {
        subscriptionService.deleteSubscription(subscriptionId);
        return ResponseEntity.ok("Subscription deleted successfully");
    }


}
