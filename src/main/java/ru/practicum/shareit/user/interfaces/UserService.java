package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.dto.UserDto;

/**
 * В данном интерфейсе объявлены основные методы для работы с сущностью User
 * 1. Создание/Регистрация
 * 2. Обновление
 * 3. Получение пользователя по его ID
 * 4. Удаление пользователя по полученному ID
 */

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    UserDto getUserById(Long userID);

    void removeUserById(Long userId);
}
