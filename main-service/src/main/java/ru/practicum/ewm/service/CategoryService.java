package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto requestDto);

    List<CategoryDto> findAll(int from, int size);

    CategoryDto findById(long catId);

    void deleteById(long catId);

    CategoryDto update(long id, NewCategoryDto requestDto);
}
