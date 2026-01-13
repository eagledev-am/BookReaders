package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.book.CategoryDto;
import com.eagledev.bookreaders.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);
}
