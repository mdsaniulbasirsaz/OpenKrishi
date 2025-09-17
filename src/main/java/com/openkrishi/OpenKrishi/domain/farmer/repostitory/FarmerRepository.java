package com.openkrishi.OpenKrishi.domain.farmer.repostitory;


import com.openkrishi.OpenKrishi.domain.farmer.entity.Farmer;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, UUID> {

    List<Farmer> findByNgoAndFarmerNameContainingIgnoreCaseOrPhoneContaining(Ngo ngo, String name, String phone);

}
