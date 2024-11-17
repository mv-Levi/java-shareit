package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.logging.Logger;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private static final Logger log = Logger.getLogger(UserController.class.getName());
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Создание пользователя
    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Creating user with data: " + userDto);
        return userService.createUser(userDto);
    }

    // Получение списка всех пользователей
    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        return userService.getAllUsers();
    }

    // Получение информации о пользователе
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Fetching user with ID: " + userId);
        return userService.getUser(userId);
    }

    // Обновление информации о пользователе
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Updating user with ID: " + userId + " with data: " + userDto);
        return userService.updateUser(userId, userDto);
    }

    // Удаление пользователя
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Deleting user with ID: " + userId);
        userService.deleteUser(userId);
    }
}
