package user.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    @Test
    void testToUserDto() {
        // Arrange: создаем объект User
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testToUser() {
        UserDto dto = new UserDto();
        dto.setId(2L);
        dto.setName("Jane Doe");
        dto.setEmail("jane.doe@example.com");

        User user = UserMapper.toUser(dto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getName()).isEqualTo("Jane Doe");
        assertThat(user.getEmail()).isEqualTo("jane.doe@example.com");
    }
}
