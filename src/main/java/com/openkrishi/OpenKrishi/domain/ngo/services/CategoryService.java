package com.openkrishi.OpenKrishi.domain.ngo.services;


import com.openkrishi.OpenKrishi.domain.ngo.entity.Category;
import com.openkrishi.OpenKrishi.domain.ngo.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    // Create category
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }


    // Get ALl Categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public Optional<Category> getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId);
    }

    // Search Categories by Keyword
    public List<Category> searchCategories(String keyword)
    {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(keyword);
    }


}
