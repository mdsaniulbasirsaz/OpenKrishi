package com.openkrishi.OpenKrishi.domain.ngo.repository;

import com.openkrishi.OpenKrishi.domain.ngo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    // Check if member exists by user's email
    boolean existsByUserEmail(String email);

    // Fetch the member by email
    Optional<Member> findByUserEmail(String email);


}
