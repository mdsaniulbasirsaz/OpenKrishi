package com.openkrishi.OpenKrishi.domain.user.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID addressId;

    @Getter
    @Setter
    private String street;


    @Setter
    @Getter
    private String houseNo;


    @Setter
    @Getter
    private String state;

    @Setter
    @Getter
    private String city;

    @Getter
    @Setter
    private String postCode;


    @Setter
    @Getter
    private String village;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;


}
