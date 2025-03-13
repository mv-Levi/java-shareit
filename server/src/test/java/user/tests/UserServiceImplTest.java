package user.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUserSuccess() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setName("Alice");
        userDto.setEmail("alice@example.com");

        UserDto created = userService.createUser(userDto);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Alice");
        assertThat(created.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void testGetAllUsers() {
        UserDto user1 = new UserDto();
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        userService.createUser(user1);

        UserDto user2 = new UserDto();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        userService.createUser(user2);

        List<UserDto> allUsers = userService.getAllUsers();

        assertThat(allUsers).hasSize(2);
    }

    @Test
    void testGetUserSuccess() {
        // Arrange: создаем пользователя
        UserDto userDto = new UserDto();
        userDto.setName("Bob");
        userDto.setEmail("bob@example.com");
        UserDto created = userService.createUser(userDto);

        UserDto found = userService.getUser(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Bob");
        assertThat(found.getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void testUpdateUserSuccess() {
        // Arrange: создаем пользователя
        UserDto userDto = new UserDto();
        userDto.setName("Charlie");
        userDto.setEmail("charlie@example.com");
        UserDto created = userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Charles");
        updateDto.setEmail("charles@example.com");

        UserDto updated = userService.updateUser(created.getId(), updateDto);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName()).isEqualTo("Charles");
        assertThat(updated.getEmail()).isEqualTo("charles@example.com");
    }

    @Test
    void testDeleteUserSuccess() {
        // Arrange: создаем пользователя
        UserDto userDto = new UserDto();
        userDto.setName("Diana");
        userDto.setEmail("diana@example.com");
        UserDto created = userService.createUser(userDto);

        userService.deleteUser(created.getId());

        assertThrows(NotFoundException.class, () -> userService.getUser(created.getId()));
    }
}
