package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "Ivan Ivanov", "ivan@email.com");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Ivan Ivanov");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ivan@email.com");
    }

    @Test
    void testUserDtoDeserialization() throws Exception {
        String content = "{\"id\":1, \"name\":\"Ivan Ivanov\", \"email\":\"ivan@email.com\"}";

        UserDto result = json.parse(content).getObject();

        assertThat(result.getName()).isEqualTo("Ivan Ivanov");
        assertThat(result.getEmail()).isEqualTo("ivan@email.com");
    }
}
