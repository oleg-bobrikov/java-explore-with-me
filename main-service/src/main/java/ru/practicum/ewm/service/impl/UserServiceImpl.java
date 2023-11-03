package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.util.PageRequestHelper;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto adminAddUser(NewUserDto requestDto) {
        return userMapper.toDto(userRepository.save(userMapper.toModel(requestDto)));
    }

    @Override
    public void adminRemoveUser(long userId) {
        User user = userRepository.findUserById(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> adminGetUsers(Set<Long> ids, int from, int size) {
        PageRequest page = PageRequestHelper.of(from, size);
        if (ids == null) {
            return userMapper.toDto(userRepository.findAll(page).getContent());
        } else {
            return userMapper.toDto(userRepository.findByIdIn(ids, page));
        }
    }

}
