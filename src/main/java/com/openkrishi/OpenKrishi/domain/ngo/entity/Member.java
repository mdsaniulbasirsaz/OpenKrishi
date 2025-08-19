package com.openkrishi.OpenKrishi.domain.ngo.entity;


import com.openkrishi.OpenKrishi.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @Getter
    @Setter
    private User user;



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
