package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto requestDto) {
        log.info("Attempt to save category with name {}", requestDto.getName());
        return categoryService.create(requestDto);
    }

    @PatchMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@RequestBody @Valid NewCategoryDto requestDto,
                              @PathVariable long catId) {
        log.info("Attempt to update the name of the category with identifier {} to {}", catId, requestDto.getName());
        return categoryService.update(catId, requestDto);
    }

    @DeleteMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable long catId) {
        log.info("Attempt to delete category with identifier {}", catId);
        categoryService.deleteById(catId);
    }
}
