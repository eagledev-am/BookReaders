package com.eagledev.bookreaders.services.cateogry;

import com.eagledev.bookreaders.dtos.book.CategoryDto;
import com.eagledev.bookreaders.entities.Category;
import com.eagledev.bookreaders.exceptions.ResourceNotFoundException;
import com.eagledev.bookreaders.mappers.CategoryMapper;
import com.eagledev.bookreaders.repos.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService{

    private final CategoryRepo repo;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getAllCatalogs() {
        return repo.findAll()
                .stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategory(String tag) {
        return categoryMapper.toCategoryDto(
                repo.findByTag(tag.toLowerCase())
                        .orElseThrow(
                                () ->     new ResourceNotFoundException("Category", "tag" , tag.toLowerCase())
                        )
        );
    }

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryDto request) {
        if(repo.existsByTag(request.getTag().toLowerCase())){
            throw new IllegalArgumentException("Catalog with name '" + request.getTag() + "' already exists.");
        }
        Category category = Category.builder()
                .tag(request.getTag().toLowerCase())
                .description(request.getDescription())
                .build();
        repo.save(category);
        return categoryMapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(String tag, CategoryDto request) {
        Category category = repo.findByTag(tag.toLowerCase())
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Category", "tag" , tag.toLowerCase());
                });
        category.setTag(request.getTag().toLowerCase());
        category.setDescription(request.getDescription());
        repo.save(category);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCatalog(String tag) {
        Category category = repo.findByTag(tag.toLowerCase())
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Category", "tag" , tag.toLowerCase());
                });
        repo.delete(category);
    }

    @Override
    public List<Category> getCategories(List<String> categoryTag) {
        return repo.findAllByTagIn(categoryTag);
    }


}
