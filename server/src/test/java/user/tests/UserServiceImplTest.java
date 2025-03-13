package user.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto validUserDto;

    @BeforeEach
    void setUp() {
        validUserDto = new UserDto();
        validUserDto.setName("Alice");
        validUserDto.setEmail("alice@example.com");
    }

    @Test
    void testCreateUserSuccess() {
        when(userRepository.findByEmail(validUserDto.getEmail())).thenReturn(Optional.empty());
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(validUserDto.getName());
        savedUser.setEmail(validUserDto.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto created = userService.createUser(validUserDto);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Alice", created.getName());
        assertEquals("alice@example.com", created.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_whenEmailIsNull_thenThrowBadRequestException() {
        validUserDto.setEmail(null);
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> userService.createUser(validUserDto)
        );
        assertEquals("Email is required.", ex.getMessage());
    }

    @Test
    void testCreateUser_whenEmailIsEmpty_thenThrowBadRequestException() {
        validUserDto.setEmail("");
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> userService.createUser(validUserDto)
        );
        assertEquals("Email is required.", ex.getMessage());
    }

    @Test
    void testCreateUser_whenEmailInvalidFormat_thenThrowBadRequestException() {
        validUserDto.setEmail("invalidEmailFormat");
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> userService.createUser(validUserDto)
        );
        assertEquals("Invalid email format.", ex.getMessage());
    }

    @Test
    void testCreateUser_whenEmailAlreadyExists_thenThrowConflictException() {
        when(userRepository.findByEmail(validUserDto.getEmail()))
                .thenReturn(Optional.of(new User()));

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> userService.createUser(validUserDto)
        );
        assertEquals("Email already in use.", ex.getMessage());
    }

    @Test
    void testGetAllUsers() {
        UserDto user1 = new UserDto();
        user1.setName("User One");
        user1.setEmail("user1@example.com");

        UserDto user2 = new UserDto();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");

        User savedUser1 = new User();
        savedUser1.setId(1L);
        savedUser1.setName(user1.getName());
        savedUser1.setEmail(user1.getEmail());

        User savedUser2 = new User();
        savedUser2.setId(2L);
        savedUser2.setName(user2.getName());
        savedUser2.setEmail(user2.getEmail());

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser1)
                .thenReturn(savedUser2);

        userService.createUser(user1);
        userService.createUser(user2);

        when(userRepository.findAll()).thenReturn(List.of(savedUser1, savedUser2));

        List<UserDto> allUsers = userService.getAllUsers();

        assertThat(allUsers).hasSize(2);
    }

    @Test
    void testGetUserSuccess() {
        UserDto userDto = new UserDto();
        userDto.setName("Bob");
        userDto.setEmail("bob@example.com");

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setName("Bob");
        savedUser.setEmail("bob@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userRepository.findById(3L)).thenReturn(Optional.of(savedUser));

        userService.createUser(userDto);

        UserDto found = userService.getUser(3L);

        assertNotNull(found);
        assertEquals(3L, found.getId());
        assertEquals("Bob", found.getName());
        assertEquals("bob@example.com", found.getEmail());
    }

    @Test
    void testUpdateUserSuccess() {
        UserDto userDto = new UserDto();
        userDto.setName("Charlie");
        userDto.setEmail("charlie@example.com");

        User savedUser = new User();
        savedUser.setId(4L);
        savedUser.setName("Charlie");
        savedUser.setEmail("charlie@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userRepository.findById(4L)).thenReturn(Optional.of(savedUser));

        userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Charles");
        updateDto.setEmail("charles@example.com");

        UserDto updated = userService.updateUser(4L, updateDto);

        assertNotNull(updated);
        assertEquals(4L, updated.getId());
        assertEquals("Charles", updated.getName());
        assertEquals("charles@example.com", updated.getEmail());
    }

    @Test
    void testDeleteUserSuccess() {
        UserDto userDto = new UserDto();
        userDto.setName("Diana");
        userDto.setEmail("diana@example.com");

        User savedUser = new User();
        savedUser.setId(5L);
        savedUser.setName("Diana");
        savedUser.setEmail("diana@example.com");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userRepository.existsById(5L)).thenReturn(true, false);
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        userService.createUser(userDto);
        userService.deleteUser(5L);

        assertThrows(NotFoundException.class, () -> userService.getUser(5L));
    }
}