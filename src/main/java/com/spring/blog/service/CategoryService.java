package com.spring.blog.service;

import com.spring.blog.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto getCategory(long categoryId);

    CategoryDto updateCategory(long categoryId, CategoryDto categoryDto);

    void deleteCategory(long categoryId);

    List<CategoryDto> getCategories();
}
