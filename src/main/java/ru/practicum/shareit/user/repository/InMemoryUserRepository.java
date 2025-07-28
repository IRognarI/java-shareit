package ru.practicum.shareit.user.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.userException.EmailDuplicatedException;
import ru.practicum.shareit.exception.userException.UserDuplicatedException;
import ru.practicum.shareit.exception.userException.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMappers;
import ru.practicum.shareit.user.model.User;

/**
 * В данном классе реализованы методы объявленные в интерфейсе UserService
 */

@Slf4j
@Component
public class InMemoryUserRepository {
    private final Map<Long, User> userMap = new TreeMap<>();

    /**
     * Создание/Регистрация пользователя
     * На выходе получаем UserDto
     */

    public UserDto createUser(UserDto userDto) {
        log.info("\tПолучено имя: {} и email {}", userDto.getName(), userDto.getEmail());

        boolean userExists = userMap.values()
                .stream()
                .anyMatch(user -> userDto.getEmail().equalsIgnoreCase(user.getEmail()));

        if (userExists) {
            throw new UserDuplicatedException("Email { " + userDto.getEmail() + " } - занят");
        }

        User user = UserMappers.mapToUser(userDto);
        User finalUser = user.toBuilder().id(generatedId()).build();

        log.info("""
                \tДобавлен пользователь с id: {}
                \tИмя: {}
                \tEmail: {}
                """, finalUser.getId(), finalUser.getName(), finalUser.getEmail());

        userMap.put(finalUser.getId(), finalUser);

        return UserMappers.userToDto(finalUser);
    }

    /**
     * Обновление пользователя. На выходе также получаем UserDto
     * Метод учитывает, чтобы email был уникален
     */

    public UserDto updateUser(Long userId, UserDto userDto) throws UserNotFoundException, EmailDuplicatedException {
        log.info("\tПередано ID пользователя {}\tДанные для обновления:\tЛогин: {}\tEmail: {}",
                userId, userDto.getName(), userDto.getEmail());

        Optional<User> userExists = Optional.ofNullable(userMap.get(userId));

        if (userExists.isEmpty()) {
            throw new UserNotFoundException("Пользователь с ID { " + userId + " } - не найден");
        }

        boolean emailExists = false;
        if (userDto.getEmail() != null) {

            if (userDto.getEmail().equalsIgnoreCase(userExists.get().getEmail())) {
                throw new EmailDuplicatedException(userDto.getEmail() + " - данный email у вас уже установлен");
            }

            emailExists = userMap.values()
                    .stream()
                    .anyMatch(user -> userDto.getEmail().equalsIgnoreCase(user.getEmail()));
        }

        if (emailExists) {
            throw new EmailDuplicatedException("Email { " + userDto.getEmail() + " } - занят");
        }

        User user = userExists.get().toBuilder()
                .name(userDto.getName() != null ? userDto.getName() : userExists.get().getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : userExists.get().getEmail())
                .build();

        log.info("\tОбновленный пользователь: ID: {}\tИмя: {}\tEmail: {}", user.getId(), user.getName(), user.getEmail());

        return UserMappers.userToDto(user);
    }

    /**
     * Получение всех пользователей
     */

    public List<UserDto> getUsers() {

        return userMap.values()
                .stream()
                .map(UserMappers::userToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение пользователя по ID
     */

    public UserDto getUserById(Long userID) {
        log.info("\tПолучен ID пользователя: {}\tПользователь существует - {}", userID, userMap.containsKey(userID));

        Optional<User> userExists = Optional.ofNullable(userMap.get(userID));

        if (userExists.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id { " + userID + " } - не найден");
        }

        return UserMappers.userToDto(userExists.get());
    }

    /**
     * Удаление пользователя по ID
     */

    public void removeUserById(Long userId) {
        log.info("\tПолучен ID пользователя: {}", userId);
        log.debug("\tПользователей до удаления: {}", userMap.size());

        Optional<User> user = Optional.ofNullable(userMap.get(userId));

        if (user.isEmpty()) {
            throw new UserNotFoundException("Пользователь с ID { " + userId + " } - не найден");
        }
        userMap.remove(user.get().getId());

        log.debug("\tПользователей после удаления: {}", userMap.size());
    }

    /**
     * Метод - генератор уникального ID в коллекции userMap
     */

    private Long generatedId() {

        long id = userMap.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        return id + 1;
    }
}
