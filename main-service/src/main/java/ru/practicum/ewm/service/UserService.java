package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto adminAddUser(NewUserDto requestDto);

    void adminRemoveUser(long catId);

    List<UserDto> adminGetUsers(Set<Long> ids, int from, int size);

    User findUserById(long userId);
}
