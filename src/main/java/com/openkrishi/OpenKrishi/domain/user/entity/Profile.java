package com.openkrishi.OpenKrishi.domain.user.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity(name = "UserProfile")
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID profileId;


    @OneToOne
    @JoinColumn(name="user_id", nullable = false)
    @Setter
    @Getter
    private User user;


    @Setter
    @Getter
    private String image;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    @Setter
    @Getter

    private Address address;

    @Getter
    @Setter
    private LocalDate dob;

}
