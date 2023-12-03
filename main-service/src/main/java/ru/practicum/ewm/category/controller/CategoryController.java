package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * Method for creating a category
     * @param newCategoryDto data for creation
     * @return Method returns the created category
     */
    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.create(newCategoryDto);
    }

    /**
     * Method for deleting a category by id
     * @param catId id of category to delete
     */
    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable long catId) {
        categoryService.deleteCategoryById(catId);
    }

    /**
     * Method for updating a category
     * @param catId id of category to update
     * @param categoryDto data for updating
     * @return Method returns the updated category
     */
    @PatchMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable long catId,
                              @Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.update(catId, categoryDto);
    }

    /**
     * Method for getting info about all categories
     * @return Method returns info about all categories
     */
    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> get(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                 @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return categoryService.get(from, size);
    }

    /**
     * Method for getting info about a category by id
     * @param catId id of category to get info about
     * @return Method returns info about a category
     */
    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable long catId) {
        return categoryService.getCategoryById(catId);
    }
}
