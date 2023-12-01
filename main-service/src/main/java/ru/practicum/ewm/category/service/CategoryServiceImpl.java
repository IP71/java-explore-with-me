package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.exception.CategoryNotEmptyException;
import ru.practicum.ewm.category.exception.CategoryNotFoundException;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(CategoryMapper.newCategoryDtoToCategory(newCategoryDto));
        log.info("Created category: {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        List<Event> foundEvents = eventRepository.findAllByCategoryId(id);
        if (foundEvents.size() != 0) {
            throw new CategoryNotEmptyException(category.getName());
        }
        categoryRepository.deleteById(id);
        log.info("Category with id={} was deleted", id);
    }

    @Override
    @Transactional
    public CategoryDto update(long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        log.info("Category with id={} was updated", id);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> get(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from/size, size);
        List<Category> foundCategories = categoryRepository.findAllBy(pageRequest);
        log.info("{} categories were found", foundCategories.size());
        return CategoryMapper.toCategoryDto(foundCategories);
    }

    @Override
    public CategoryDto getCategoryById(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        log.info("Get category with id={}", id);
        return CategoryMapper.toCategoryDto(category);
    }
}
