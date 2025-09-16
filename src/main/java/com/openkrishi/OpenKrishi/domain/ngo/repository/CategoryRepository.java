package com.openkrishi.OpenKrishi.domain.ngo.repository;


import com.openkrishi.OpenKrishi.domain.ngo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByCategoryNameContainingIgnoreCase(String keyword);
    boolean existsByCategoryName(String categoryName);

}
