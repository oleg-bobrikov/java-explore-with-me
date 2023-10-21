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
    public CategoryDto adminAddCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Attempt to save category with name {}", newCategoryDto.getName());
        return categoryService.adminAddCategory(newCategoryDto);
    }

    @PatchMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto adminUpdateCategory(@RequestBody @Valid NewCategoryDto requestDto,
                              @PathVariable long catId) {
        log.info("Attempt to update the name of the category with identifier {} to {}", catId, requestDto.getName());
        return categoryService.adminUpdateCategory(catId, requestDto);
    }


    @DeleteMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminRemoveCategory(@PathVariable long catId) {
        log.info("Attempt to delete category with identifier {}", catId);
        categoryService.adminRemoveCategory(catId);
    }
}
