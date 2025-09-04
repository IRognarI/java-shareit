package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * Объявлены основные CRUD методы
 *
 */

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUserById(Long id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    void delete(Long userId);

    User findUserById(Long id);
}
