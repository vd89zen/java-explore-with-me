package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.NewCategoryDto;
import ru.practicum.ewm.dto.common.CategoryDto;
import ru.practicum.ewm.service.api.CategoryService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.addCategory(newCategoryDto));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long catId) {
        categoryService.deleteCategory(catId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable long catId,
                                                      @Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(
                categoryService.updateCategory(catId, categoryDto));
    }
}
