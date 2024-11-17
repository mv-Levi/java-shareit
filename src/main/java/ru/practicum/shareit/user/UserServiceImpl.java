package ru.practicum.shareit.user;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Long, User> userDatabase = new HashMap<>();
    private final Set<String> emailSet = new HashSet<>();
    private long idCounter = 1;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    @PostConstruct
    public void initEmailSet() {
        userDatabase.values().forEach(user -> emailSet.add(user.getEmail()));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new BadRequestException("Email is required.");
        }

        if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
            throw new BadRequestException("Invalid email format.");
        }

        if (emailSet.contains(userDto.getEmail())) {
            throw new ConflictException("Email already in use.");
        }

        User user = UserMapper.toUser(userDto);
        user.setId(idCounter++);
        userDatabase.put(user.getId(), user);
        emailSet.add(user.getEmail());
        return UserMapper.toUserDto(user);
    }


    @Override
    public List<UserDto> getAllUsers() {
        return userDatabase.values().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userDatabase.get(userId);
        return user != null ? UserMapper.toUserDto(user) : null;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userDatabase.get(userId);
        if (user == null) {
            throw new NotFoundException("User not found.");
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
                throw new BadRequestException("Invalid email format.");
            }

            if (!user.getEmail().equals(userDto.getEmail())) {
                if (emailSet.contains(userDto.getEmail())) {
                    throw new ConflictException("Email already in use by another user.");
                }

                emailSet.remove(user.getEmail());
                emailSet.add(userDto.getEmail());
                user.setEmail(userDto.getEmail());
            }
        }

        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            user.setName(userDto.getName());
        }

        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userDatabase.remove(userId);
        if (user != null) {
            emailSet.remove(user.getEmail());
        }
    }
}
