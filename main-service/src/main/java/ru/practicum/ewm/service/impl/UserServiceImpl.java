package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserService;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto adminAddUser(NewUserDto requestDto) {
        return userMapper.toDto(userRepository.save(userMapper.toModel(requestDto)));
    }

    @Override
    public void adminRemoveUser(long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }

    @Override
    public List<UserDto> adminGetUsers(Set<Long> ids, int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        if (ids == null) {
            return userMapper.toDto(userRepository.findAll(page).getContent());
        } else {
            return userMapper.toDto(userRepository.findByIdIn(ids, page));
        }
    }
    @Override
    public User findUserById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No user found with identifier %s", id)));
    }
}
