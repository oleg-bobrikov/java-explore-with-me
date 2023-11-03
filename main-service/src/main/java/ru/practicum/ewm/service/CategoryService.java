package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto adminAddCategory(NewCategoryDto requestDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);

    void adminRemoveCategory(long catId);

    CategoryDto adminUpdateCategory(long id, NewCategoryDto requestDto);
}
