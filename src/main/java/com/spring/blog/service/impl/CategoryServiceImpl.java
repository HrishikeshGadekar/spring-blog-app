package com.spring.blog.service.impl;

import com.spring.blog.dto.CategoryDto;
import com.spring.blog.entity.Category;
import com.spring.blog.exceptions.ResourceNotFoundException;
import com.spring.blog.repository.CategoryRepository;
import com.spring.blog.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    private ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = mapToCategoryEntity(categoryDto);
        Category createdCategory = categoryRepository.save(category);
        return mapToCategoryDto(createdCategory);
    }

    @Override
    public CategoryDto getCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                                              .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", categoryId));
        return mapToCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId)
                                              .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", categoryId));

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        return mapToCategoryDto(updatedCategory);
    }

    @Override
    public void deleteCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                                              .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", categoryId));
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll()
                                 .stream()
                                 .map((category) -> mapToCategoryDto(category))
                                 .collect(Collectors.toList());
    }

    private Category mapToCategoryEntity(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }

    private CategoryDto mapToCategoryDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }


}
