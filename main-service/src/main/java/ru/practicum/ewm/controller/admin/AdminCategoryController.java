package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.util.PrintLogs;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@Slf4j
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;
    private final PrintLogs printLogs;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto adminAddCategory(@RequestBody @Valid NewCategoryDto newCategoryDto,
                                        HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to save category with name {}", newCategoryDto.getName());

        return categoryService.adminAddCategory(newCategoryDto);
    }

    @PatchMapping(path = "/{catId}")
    public CategoryDto adminUpdateCategory(@RequestBody @Valid NewCategoryDto requestDto,
                                           @PathVariable @Positive long catId,
                                           HttpServletRequest httpServletRequest) {

        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to update the name of the category with identifier {} to {}", catId, requestDto.getName());

        return categoryService.adminUpdateCategory(catId, requestDto);
    }


    @DeleteMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminRemoveCategory(@PathVariable @Positive long catId, HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to delete category with identifier {}", catId);

        categoryService.adminRemoveCategory(catId);
    }
}
