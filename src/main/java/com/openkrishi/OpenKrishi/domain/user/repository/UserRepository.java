package com.openkrishi.OpenKrishi.domain.user.repository;

import com.openkrishi.OpenKrishi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
}
