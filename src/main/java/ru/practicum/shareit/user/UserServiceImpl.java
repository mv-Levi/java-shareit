package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService{
    private final Map<Long, User> userDatabase = new HashMap<>();
    private long idCounter = 1;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new BadRequestException("Email is required.");
        }

        if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
            throw new BadRequestException("Invalid email format.");
        }

        for (User user : userDatabase.values()) {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new ConflictException("Email already in use.");
            }
        }

        User user = UserMapper.toUser(userDto);
        user.setId(idCounter++);
        userDatabase.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userDatabase.values()) {
            users.add(UserMapper.toUserDto(user));
        }
        return users;
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userDatabase.get(userId);
        return user != null ? UserMapper.toUserDto(user) : null;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userDatabase.get(userId);
        if (user != null) {
            for (User existingUser : userDatabase.values()) {
                if (!existingUser.getId().equals(userId) && existingUser.getEmail().equals(userDto.getEmail())) {
                    throw new ConflictException("Email already in use by another user.");
                }
            }

            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            return UserMapper.toUserDto(user);
        }
        return null;
    }

    @Override
    public void deleteUser(Long userId) {
        userDatabase.remove(userId);
    }
}
