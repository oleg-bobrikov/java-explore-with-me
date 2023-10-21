package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto adminAddCategory(NewCategoryDto requestDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toModel(requestDto)));
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryMapper.toDto(categoryRepository.findAll(page).getContent());
    }

    @Override
    public CategoryDto getCategory(long id) {
        return categoryMapper.toDto(findCategoryById(id));
    }

    @Override
    public void adminRemoveCategory(long id) {
        Category category = findCategoryById(id);
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto adminUpdateCategory(long id, NewCategoryDto requestDto) {
        Category category = findCategoryById(id);
        Category updatedCategory = category.toBuilder().name(requestDto.getName()).build();
        return categoryMapper.toDto(categoryRepository.save(updatedCategory));
    }

    @Override
    public Category findCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No category found with identifier %s", id)));
    }
}
