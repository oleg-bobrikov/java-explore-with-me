package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;

import ru.practicum.ewm.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_SIZE;

@RestController
@Slf4j
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                     @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size) {
        log.info("Get all categories with page from = {} and size = {}", from , size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable long catId) {
        log.info("Get category by id {}", catId);
        return categoryService.getCategory(catId);
    }
}
