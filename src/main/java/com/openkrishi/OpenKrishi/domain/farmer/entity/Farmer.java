package com.openkrishi.OpenKrishi.domain.farmer.entity;


import com.openkrishi.OpenKrishi.domain.ngo.entity.Member;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@Entity
@Table(name = "farmers")
public class Farmer {

    @Id
    @UuidGenerator
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;


    @Getter
    @Setter
    private String farmerName;


    @Getter
    @Setter
    private String phone;


    @Getter
    @Setter
    private Double latitude;

    @Setter
    @Getter
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @Getter
    @Setter
    private Member createdBy;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id",nullable = false)
    @Getter
    @Setter
    private Ngo ngo;


}
