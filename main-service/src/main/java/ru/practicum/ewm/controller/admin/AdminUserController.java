package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.util.PrintLogs;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;

import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_SIZE;

@RestController
@Slf4j
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
public class AdminUserController {
    private final UserService userService;
    private final PrintLogs printLogs;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto adminAddUser(@RequestBody @Valid NewUserDto requestDto,
                                HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Admin is adding user with email {} and name {}", requestDto.getEmail(), requestDto.getName());

        return userService.adminAddUser(requestDto);
    }

    @DeleteMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminRemoveUser(@PathVariable @Positive long userId, HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Admin is removing user with identifier {}", userId);

        userService.adminRemoveUser(userId);
    }

    @GetMapping
    public List<UserDto> adminGetUsers(@RequestParam(required = false) Set<Long> ids,
                                       @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                       @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size,
                                       HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Admin is getting all users by ids = {}, page from = {}, size = {}", ids, from, size);

        return userService.adminGetUsers(ids, from, size);
    }
}
