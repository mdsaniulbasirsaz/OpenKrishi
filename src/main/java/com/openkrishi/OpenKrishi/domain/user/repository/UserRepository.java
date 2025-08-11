package com.openkrishi.OpenKrishi.domain.user.repository;

import com.openkrishi.OpenKrishi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
