package ru.practicum.ewm.service.api;

import ru.practicum.ewm.dto.request.NewCategoryDto;
import ru.practicum.ewm.dto.common.CategoryDto;
import ru.practicum.ewm.model.Category;

import java.util.List;

public interface CategoryService {

    // методы для админа
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    void deleteCategory(Long catId);

    // публичные методы
    List<CategoryDto> getCategories(int from, int size);

    Category getCategoryById(Long catId);

    CategoryDto getCategoryDtoById(Long catId);
}