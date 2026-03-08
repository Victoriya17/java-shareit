package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(NewUserRequest userRequest);

    UserDto updateUser(Long userId, UpdateUserRequest userRequest);

    Collection<UserDto> findAllUsers();

    UserDto findUserById(Long userId);

    boolean deleteUser(Long userId);
}
