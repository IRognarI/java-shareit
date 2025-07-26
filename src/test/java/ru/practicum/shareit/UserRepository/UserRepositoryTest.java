package ru.practicum.shareit.UserRepository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.userException.EmailDuplicatedException;
import ru.practicum.shareit.exception.userException.UserDuplicatedException;
import ru.practicum.shareit.exception.userException.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
public class UserRepositoryTest {
    private UserRepository repository;
    private UserDto testUser1;
    private UserDto testUser2;
    private UserDto testUser3;
    private UserDto testUser4;
    private UserDto testUser5;

    @BeforeEach
    public void setup() {
        repository = new UserRepository();

        testUser1 = UserDto.builder()
                .name("Aleks")
                .email("example@mail.ru")
                .build();

        testUser2 = UserDto.builder()
                .name("Irina")
                .email("fjfigjrei@gmail.com")
                .build();

        testUser3 = UserDto.builder().email("correctedEmail@mail.ru").build();

        testUser4 = UserDto.builder().name("Iren").build();

        testUser5 = UserDto.builder()
                .name("Valya")
                .email(testUser1.getEmail())
                .build();

    }

    @Test
    public void creatingCorrectUser() {
        UserDto user = repository.createUser(testUser1);

        long userId = user.getId();

        UserDto userExists = repository.getUserById(userId);

        Assertions.assertNotNull(userExists);
    }

    @Test
    public void correctUpdateOfUserAddress() {
        UserDto userDto = repository.createUser(testUser2);

        UserDto userUpdate = repository.updateUser(userDto.getId(), testUser3);

        Assertions.assertEquals("correctedEmail@mail.ru", userUpdate.getEmail());

    }

    @Test
    public void unCorrectUpdateOfUserAddress() {
        UserDto userDto = repository.createUser(testUser2);
        UserDto userDto2 = repository.createUser(testUser1);

        Assertions.assertThrows(EmailDuplicatedException.class, () -> repository.updateUser(userDto.getId(),
                userDto2));

    }

    @Test
    public void correctUpdateOfUserName() {
        UserDto userDto = repository.createUser(testUser2);

        UserDto userUpdate = repository.updateUser(userDto.getId(), testUser4);

        Assertions.assertTrue(userUpdate.getName().equalsIgnoreCase(testUser4.getName()));
    }

    @Test
    public void creatingUserWithAnAlreadyExistingAddress() {
        repository.createUser(testUser1);

        Assertions.assertThrows(UserDuplicatedException.class, () -> repository.createUser(testUser5));
    }

    @Test
    public void removingExistingUser() {
        repository.createUser(testUser1);
        UserDto user2 = repository.createUser(testUser2);

        Assertions.assertEquals(2, repository.getUsers().size());

        repository.removeUserById(user2.getId());

        Assertions.assertEquals(1, repository.getUsers().size());
    }

    @Test
    public void getUserById() {
        repository.createUser(testUser1);
        UserDto user2 = repository.createUser(testUser2);

        UserDto user = repository.getUserById(user2.getId());

        Assertions.assertEquals("Irina", user.getName());
    }

    @Test
    public void anAttemptToGetNonExistentUser() {
        Assertions.assertThrows(UserNotFoundException.class, () -> repository.getUserById(4L));
    }
}
