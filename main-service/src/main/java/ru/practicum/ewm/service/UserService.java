package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto create(NewUserDto requestDto);

    void deleteById(long catId);

    List<UserDto> findUsersByIds(Set<Long> ids, int from, int size);
}
