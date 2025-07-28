package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

/**
 * Класс имплементирующий интерфейс UserService
 * Реализация методов находится в классе InMemoryUserRepository
 */

@Service
@RequiredArgsConstructor
public class InMemoryUserServiceImpl implements UserService {

    private final InMemoryUserRepository repository;

    @Override
    public UserDto createUser(UserDto userDto) {
        return repository.createUser(userDto);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (userId == null) {
            throw new ValidationException("ID пользователя должен быть указан");
        }

        if (userDto == null) {
            throw new ValidationException("Не достаточно данных для обновления пользователя");
        }

        return repository.updateUser(userId, userDto);
    }

    @Override
    public UserDto getUserById(Long userID) {
        if (userID == null) {
            throw new ValidationException("ID пользователя должен быть указан");
        }

        return repository.getUserById(userID);
    }

    @Override
    public void removeUserById(Long userId) {
        if (userId == null) {
            throw new ValidationException("Для удаления пользователя укажите ID");
        }
        repository.removeUserById(userId);
    }
}
