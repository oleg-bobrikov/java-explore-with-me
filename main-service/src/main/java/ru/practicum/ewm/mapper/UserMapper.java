package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;

import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    User toModel(NewUserDto userDto);
}
