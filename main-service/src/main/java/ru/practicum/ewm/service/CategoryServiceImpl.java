package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.NewCategoryDto;
import ru.practicum.ewm.dto.common.CategoryDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.utils.NotFound;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.service.api.CategoryService;
import ru.practicum.ewm.service.api.ValidationService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ValidationService validationService;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавляем новую категорию: {}", newCategoryDto);
        validationService.checkCategoryNameUse(newCategoryDto.getName(), null);
        Category newCategory = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        log.info("Новая категория добавлена: {}", newCategory);
        return CategoryMapper.toCategoryDto(newCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("Обновляем категорию id {}: {}", catId, categoryDto);
        Category category = getCategoryOrThrow(catId);
        log.info("old name: {}", category.getName());
        validationService.checkCategoryNameUse(categoryDto.getName(), catId);
        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        log.info("Категория id {} обновлена", catId);
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("Удаляем категорию id {}", catId);
        validationService.validateCategoryDeletion(catId);
        categoryRepository.deleteById(catId);
        log.info("Категория id {} удалена", catId);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        log.info("Получаем категории - from: {}, size: {}", from, size);
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by("id").ascending());
        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public Category getCategoryById(Long catId) {
        log.info("Получаем категорию id {}", catId);
        return getCategoryOrThrow(catId);
    }

    @Override
    public CategoryDto getCategoryDtoById(Long catId) {
        log.info("Получаем DTO категории id {}", catId);
        return CategoryMapper.toCategoryDto(
                getCategoryById(catId));
    }

    private Category getCategoryOrThrow(Long catId) {
        log.info("Получаем сущность категории id {}", catId);
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(NotFound.CATEGORY, catId)));
    }
}
