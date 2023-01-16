package com.spring.blog.controller;

import com.spring.blog.dto.CategoryDto;
import com.spring.blog.dto.CommentDto;
import com.spring.blog.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.spring.blog.utils.Constants.*;

@RestController
@RequestMapping(CATEGORY_BASE_URL)
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //        @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.addCategory(categoryDto), HttpStatus.CREATED);
    }

    @GetMapping(GET_CATEGORY)
    public ResponseEntity<CategoryDto> getCategory(@PathVariable long categoryId) {
        return new ResponseEntity<>(categoryService.getCategory(categoryId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories() {
        return new ResponseEntity<>(categoryService.getCategories(), HttpStatus.OK);
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(UPDATE_CATEGORY)
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable long categoryId,
                                                      @Valid @RequestBody CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.updateCategory(categoryId, categoryDto), HttpStatus.ACCEPTED);
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(DELETE_CATEGORY)
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable long categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
