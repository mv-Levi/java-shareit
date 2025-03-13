package user.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = ru.practicum.shareit.ShareItServer.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUser() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("Alice");
        inputDto.setEmail("alice@example.com");
        UserDto outputDto = new UserDto();
        outputDto.setId(1L);
        outputDto.setName("Alice");
        outputDto.setEmail("alice@example.com");

        Mockito.when(userService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(outputDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserDto user1 = new UserDto(1L, "Alice", "alice@example.com");
        UserDto user2 = new UserDto(2L, "Bob", "bob@example.com");
        List<UserDto> users = List.of(user1, user2);

        Mockito.when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void testGetUserById() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "Alice", "alice@example.com");

        Mockito.when(userService.getUser(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void testUpdateUser() throws Exception {
        Long userId = 1L;
        UserDto updateDto = new UserDto();
        updateDto.setName("Alice Updated");
        updateDto.setEmail("alice_updated@example.com");

        UserDto updatedUser = new UserDto(userId, "Alice Updated", "alice_updated@example.com");

        Mockito.when(userService.updateUser(Mockito.eq(userId), Mockito.any(UserDto.class)))
                .thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Alice Updated"))
                .andExpect(jsonPath("$.email").value("alice_updated@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        Long userId = 1L;
        Mockito.doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
    }
}
