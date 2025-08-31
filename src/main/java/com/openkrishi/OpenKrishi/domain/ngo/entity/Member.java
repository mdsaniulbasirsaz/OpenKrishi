package com.openkrishi.OpenKrishi.domain.ngo.entity;


import com.openkrishi.OpenKrishi.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;


    @Column(nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(nullable = false)
    @Getter
    @Setter
    private String phone;

    @Column(nullable = false)
    @Getter
    @Setter
    private String address;


    @Column
    @Getter
    @Setter
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id", nullable = false)
    @Getter
    @Setter
    private Ngo ngo;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private MemberDesignation memberDesignation;


    public enum MemberDesignation{
        FARMER_MANAGER,
        DELIVERY_AGENT,
        VOLUNTEER
    }
}
