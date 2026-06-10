package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class UserMapperTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@mail.com");
    }

    @Test
    void mapToUserDtoTest() {
        UserDto result = UserMapper.mapToUserDto(user);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("John"));
        assertThat(result.getEmail(), equalTo("john@mail.com"));
    }

    @Test
    void mapToUserTest() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Alice");
        request.setEmail("alice@mail.com");

        User result = UserMapper.mapToUser(request);

        assertThat(result.getName(), equalTo("Alice"));
        assertThat(result.getEmail(), equalTo("alice@mail.com"));
        assertThat(result.getId(), is(nullValue()));
    }

    @Test
    void updateUserFieldsTest() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("new@mail.com");

        User result = UserMapper.updateUserFields(user, updateRequest);

        assertThat(result.getName(), equalTo("John Updated"));
        assertThat(result.getEmail(), equalTo("new@mail.com"));
    }

    @Test
    void updateUserFieldsOnlyNameTest() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Only Name Updated");

        User result = UserMapper.updateUserFields(user, updateRequest);

        assertThat(result.getName(), equalTo("Only Name Updated"));
        assertThat(result.getEmail(), equalTo("john@mail.com"));
    }

    @Test
    void updateUserFieldsOnlyEmailTest() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setEmail("only-new-email@mail.com");

        User result = UserMapper.updateUserFields(user, updateRequest);

        assertThat(result.getEmail(), equalTo("only-new-email@mail.com"));
        assertThat(result.getName(), equalTo("John"));
    }
}