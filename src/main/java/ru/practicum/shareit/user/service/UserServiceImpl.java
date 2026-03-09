package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto createUser(NewUserRequest userRequest) {
        log.debug("Создание нового пользователя {}", userRequest.getName());
        validateUniqueEmail(userRequest.getEmail());

        User user = UserMapper.mapToUser(userRequest);
        user = userStorage.createUser(user);

        return UserMapper.mapToUserDto(user);
    }

    private void validateUniqueEmail(String email) {
        userStorage.findUserByEmail(email)
                .ifPresent(user -> {
                    throw new DuplicatedDataException("Эта почта уже используется.");
                });
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUserRequest userRequest) {
        User existingUser = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userRequest.getEmail() != null && !userRequest.getEmail().equals(existingUser.getEmail())) {
            validateUniqueEmail(userRequest.getEmail());
        }
        User updatedUser = UserMapper.updateUserFields(existingUser, userRequest);
        updatedUser = userStorage.updateUser(updatedUser);

        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public Collection<UserDto> findAllUsers() {
        log.debug("Получение списка пользователей");
        return userStorage.findAllUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long userId) {
        log.debug("Поиск пользователя c ID{}", userId);
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public boolean deleteUser(Long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        log.debug("Удаление данных пользователя {}", user.getName());
        return userStorage.deleteUser(userId);
    }
}
