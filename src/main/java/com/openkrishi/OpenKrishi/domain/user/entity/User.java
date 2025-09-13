package com.openkrishi.OpenKrishi.domain.user.entity;

import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
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

    @Setter
    @Getter
    private Double latitude;

    @Setter
    @Getter
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    @Getter
    private Role role;

    @Setter
    @Getter
    private boolean isDelete;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus isSubscribed;

    @Getter
    private LocalDateTime createdAt;

    @Getter
    private LocalDateTime updatedAt;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Ngo ngo;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Profile profile;


    @PrePersist
    public void onCreate() {
        LocalDateTime now  = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        isDelete = false;
        isSubscribed = SubscriptionStatus.NO;
        if (status == null) {
            if (role == Role.CUSTOMER) {
                status = Status.ACTIVE;
            } else {
                status = Status.INACTIVE;
            }
        }
    }

    private String generateCustomId() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return "OK-" + number;
    }

    public enum Role {
        CUSTOMER,
        NGO,
        FARMER,
        ADMIN
    }

    public enum Status {
        INACTIVE,
        ACTIVE
    }

    public enum SubscriptionStatus {
        NO,
        YES
    }
}
