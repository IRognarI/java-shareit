package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.UserAlreadyExistsException;
import ru.practicum.shareit.booking.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Реализация методов интерфейса UserService
 * Основные CRUD операции и получение сущностей по их Id
 *
 */

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.mapper = userMapper;
    }

    @Override
    public List<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return mapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + id + " не найден!")));
    }

    @Override
    public UserDto create(UserDto userDto) {
        Optional<User> user = repository.getUserByEmail(userDto.getEmail());

        if (user.isPresent()) throw new UserAlreadyExistsException("Пользователь с E-mail=" +
                userDto.getEmail() + " уже существует!");

        return mapper.toUserDto(repository.save(mapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + id + " не найден!"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if ((userDto.getEmail() != null) && (userDto.getEmail() != user.getEmail())) {
            if (repository.findByEmail(userDto.getEmail())
                    .stream()
                    .filter(u -> u.getEmail().equals(userDto.getEmail()))
                    .allMatch(u -> u.getId().equals(userDto.getId()))) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new UserAlreadyExistsException("Пользователь с E-mail=" + user.getEmail() + " уже существует!");
            }

        }
        return mapper.toUserDto(repository.save(user));
    }

    @Override
    public void delete(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + userId + " не найден!"));

        repository.deleteById(user.getId());
    }

    @Override
    public User findUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + id + " не найден!"));
    }
}
