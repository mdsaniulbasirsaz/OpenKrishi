package com.openkrishi.OpenKrishi.domain.user.repository;

import com.openkrishi.OpenKrishi.domain.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Profile findByUserId(UUID userId);
}
