package com.openkrishi.OpenKrishi.domain.ngo.entity;


import com.openkrishi.OpenKrishi.domain.user.entity.Address;
import com.openkrishi.OpenKrishi.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "ngos")
public class Ngo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID ngoId;


    @Setter
    @Getter
    private String managerName;


    @Getter
    @Setter
    private String licenceUrl;



    @OneToOne
    @JoinColumn(name = "user_id",nullable = false)
    @Setter
    @Getter
    private User user;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "address_id", nullable = true)
    @Setter @Getter
    private Address address;
}
