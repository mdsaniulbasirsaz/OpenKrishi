package com.openkrishi.OpenKrishi.domain.farmer.repostitory;


import com.openkrishi.OpenKrishi.domain.farmer.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, UUID> {

}
