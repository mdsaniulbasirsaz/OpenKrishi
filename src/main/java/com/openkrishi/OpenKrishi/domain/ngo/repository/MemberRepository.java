package com.openkrishi.OpenKrishi.domain.ngo.repository;

import com.openkrishi.OpenKrishi.domain.ngo.entity.Member;
import com.openkrishi.OpenKrishi.domain.ngo.entity.Ngo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    List<Member> findAllByNgo(Ngo ngo);
}
