package com.openkrishi.OpenKrishi.domain.user.repository;


import com.openkrishi.OpenKrishi.domain.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    Optional<Address> findByUser_Id(UUID userId);

}
