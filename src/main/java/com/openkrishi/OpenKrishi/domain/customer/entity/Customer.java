package com.openkrishi.OpenKrishi.domain.customer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @UuidGenerator
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Setter
    @Getter
    @Column(nullable = false)
    private String fullName;

    @Setter
    @Getter
    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Getter
    @Column(nullable = false)
    private String password;

    @Setter
    @Getter
    private String phone;

    @Builder.Default
    @Getter
    @ManyToMany(mappedBy = "customers")
    private Set<Subscription> subscriptions = new HashSet<>();

    @Setter
    @Getter
    private Double latitude;

    @Setter
    @Getter
    private Double longitude;

    @Getter
    private LocalDateTime createdAt;

    @Getter
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now  = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
}
