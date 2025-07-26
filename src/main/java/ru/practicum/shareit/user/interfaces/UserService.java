package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    UserDto getUserById(Long userID);

    void removeUserById(Long userId);
}
