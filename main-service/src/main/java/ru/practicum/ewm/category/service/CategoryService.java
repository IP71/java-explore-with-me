package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    void deleteCategoryById(long id);

    CategoryDto update(long id, CategoryDto categoryDto);

    List<CategoryDto> get(int from, int size);

    CategoryDto getCategoryById(long id);
}
