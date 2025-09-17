package com.openkrishi.OpenKrishi.domain.ngo.repository;

import com.openkrishi.OpenKrishi.domain.ngo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {}