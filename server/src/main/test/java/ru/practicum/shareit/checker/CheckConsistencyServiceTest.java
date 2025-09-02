package ru.practicum.shareit.checker;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CheckConsistencyServiceTest {
    private final CheckConsistencyService checker;

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private User user = new User(1L, "User", "first@first.ru");
    private User user2 = new User(2L, "Second", "second@second.ru");
    private Item item = new Item(1L, "Item1", "Description1", true, user, null);

    @Test
    void shouldReturnTrueWhenExistUser() {
        UserDto newUserDto = userService.create(UserMapper.toUserDto(user));
        assertTrue(checker.isExistUser(newUserDto.getId()));
    }

    @Test
    void shouldReturnTrueWhenItemAvailable() {
        UserDto newUserDto = userService.create(UserMapper.toUserDto(user));
        ItemDto newItemDto = itemService.create(ItemMapper.toItemDto(item, List.of()), newUserDto.getId());
        assertTrue(checker.isAvailableItem(newItemDto.getId()));
    }

    @Test
    void shouldReturnTrueWhenIsItemOwner() {
        UserDto newUserDto = userService.create(UserMapper.toUserDto(user));
        ItemDto newItemDto = itemService.create(ItemMapper.toItemDto(item, List.of()), newUserDto.getId());
        assertTrue(checker.isItemOwner(newItemDto.getId(), newUserDto.getId()));
    }

    @Test
    void shouldReturnBookingWithUserBookedItem() {
        UserDto firstUserDto = userService.create(UserMapper.toUserDto(user));
        UserDto secondUserDto = userService.create(UserMapper.toUserDto(user2));
        ItemDto newItemDto = itemService.create(ItemMapper.toItemDto(item, List.of()), firstUserDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(3)
        );
        BookingDto bookingDto = bookingService.create(bookingInputDto, secondUserDto.getId());
        bookingService.update(bookingDto.getId(), firstUserDto.getId(), true);
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(checker.getBookingWithUserBookedItem(newItemDto.getId(),
                secondUserDto.getId()).getId(), bookingDto.getId());
    }

}