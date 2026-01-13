package com.eagledev.bookreaders.services.cateogry;

import com.eagledev.bookreaders.dtos.book.CategoryDto;
import com.eagledev.bookreaders.entities.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCatalogs();
    CategoryDto getCategory(String tag);
    CategoryDto createCategory(CategoryDto request);
    CategoryDto updateCategory(String tag ,CategoryDto request);
    void deleteCatalog(String tag);
    List<Category> getCategories(List<String> categoryTag);
}
