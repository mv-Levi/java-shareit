package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto getUser(Long userId);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

}
