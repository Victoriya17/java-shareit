package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@mail.com");
    }

    @Test
    void createUserSuccess() {
        NewUserRequest request = new NewUserRequest();
        request.setName("John");
        request.setEmail("john@mail.com");

        when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(request);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getEmail(), equalTo(request.getEmail()));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserThrowsExceptionWhenEmailExists() {
        NewUserRequest request = new NewUserRequest();
        request.setEmail("john@mail.com");

        when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(DuplicatedDataException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findUserByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findUserById(1L);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("John"));
    }

    @Test
    void findUserByIdThrowsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(99L));
    }

    @Test
    void findAllUsersSuccess() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        Collection<UserDto> result = userService.findAllUsers();

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getName(), equalTo("John"));
    }

    @Test
    void updateUserAllFieldsSuccess() {
        UpdateUserRequest update = new UpdateUserRequest();
        update.setName("Updated");
        update.setEmail("new@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findUserByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, update);

        assertThat(result.getName(), equalTo("Updated"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserThrowsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(1L, new UpdateUserRequest()));
    }

    @Test
    void deleteUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }
}
