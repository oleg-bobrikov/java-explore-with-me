package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto adminAddCategory(NewCategoryDto requestDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toModel(requestDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryMapper.toDto(categoryRepository.findAll(page).getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(long id) {
        return categoryMapper.toDto(categoryRepository.findCategoryById(id));
    }

    @Override
    public void adminRemoveCategory(long id) {
        Category category = categoryRepository.findCategoryById(id);
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto adminUpdateCategory(long id, NewCategoryDto requestDto) {
        Category category = categoryRepository.findCategoryById(id);
        if (category.getName().equals(requestDto.getName())){
            return categoryMapper.toDto(category);
        }

        Category updatedCategory = category.toBuilder().name(requestDto.getName()).build();
        return categoryMapper.toDto(categoryRepository.save(updatedCategory));
    }
}
