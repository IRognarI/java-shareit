package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private User user = new User(200L, "First", "first@first200.ru");
    private UserDto userDto1 = new UserDto(201L, "AlexOne", "alexone@alex200.ru");
    private UserDto userDto2 = new UserDto(202L, "AlexTwo", "alextwo@alex200.ru");
    private ItemDto itemDto = new ItemDto(200L, "Item1", "Description1", true,
            user, null, null, null, null);
    private ItemDto itemDto2 = new ItemDto(202L, "Item2", "Description2", true,
            user, null, null, null, null);

    @Test
    void shouldCreateItem() {
        UserDto newUserDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(itemDto, newUserDto.getId());
        ItemDto returnItemDto = itemService.getItemById(newItemDto.getId(), newUserDto.getId());
        assertThat(returnItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void shouldDeleteItemWhenUserNotOwner() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto, ownerDto.getId());
        ItemNotFoundException exp = assertThrows(ItemNotFoundException.class,
                () -> itemService.delete(newItemDto.getId(), newUserDto.getId()));
        assertEquals("У пользователя нет такой вещи!", exp.getMessage());
    }

    @Test
    void shouldDeleteWhenUserIsOwner() {
        UserDto ownerDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(itemDto, ownerDto.getId());
        itemService.delete(newItemDto.getId(), ownerDto.getId());
        ItemNotFoundException exp = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemById(newItemDto.getId(), ownerDto.getId()));
        assertEquals("Вещь с ID=" + newItemDto.getId() + " не найдена!", exp.getMessage());
    }

    @Test
    void shouldExceptionWhenDeleteItemNotExist() {
        UserDto ownerDto = userService.create(userDto1);
        ItemNotFoundException exp = assertThrows(ItemNotFoundException.class,
                () -> itemService.delete(-2L, ownerDto.getId()));
        assertEquals("Вещь с ID=-2 не найдена!", exp.getMessage());
    }

    @Test
    void shouldUpdateItem() {
        UserDto newUserDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(itemDto, newUserDto.getId());
        newItemDto.setName("NewName");
        newItemDto.setDescription("NewDescription");
        newItemDto.setAvailable(false);
        ItemDto returnItemDto = itemService.update(newItemDto, newUserDto.getId(), newItemDto.getId());
        assertThat(returnItemDto.getName(), equalTo("NewName"));
        assertThat(returnItemDto.getDescription(), equalTo("NewDescription"));
        assertFalse(returnItemDto.getAvailable());
    }

    @Test
    void shouldExceptionWhenUpdateItemNotOwner() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto, ownerDto.getId());
        ItemNotFoundException exp = assertThrows(ItemNotFoundException.class,
                () -> itemService.update(newItemDto, newUserDto.getId(), newItemDto.getId()));
        assertEquals("У пользователя нет такой вещи!", exp.getMessage());
    }

    @Test
    void shouldReturnItemsByOwner() {
        UserDto ownerDto = userService.create(userDto1);
        itemService.create(itemDto, ownerDto.getId());
        itemService.create(itemDto2, ownerDto.getId());
        List<ItemDto> listItems = itemService.getItemsByOwner(ownerDto.getId());
        assertEquals(2, listItems.size());
    }

    @Test
    void shouldReturnItemsBySearch() {
        UserDto ownerDto = userService.create(userDto1);
        itemService.create(itemDto, ownerDto.getId());
        itemService.create(itemDto2, ownerDto.getId());
        List<ItemDto> listItems = itemService.getItemsBySearchQuery("item");
        assertEquals(2, listItems.size());
    }

    @Test
    void shouldReturnItemsBySearchWhenSizeIsNull() {
        UserDto ownerDto = userService.create(userDto1);
        itemService.create(itemDto, ownerDto.getId());
        itemService.create(itemDto2, ownerDto.getId());
        List<ItemDto> listItems = itemService.getItemsBySearchQuery("item");
        assertEquals(2, listItems.size());
    }

    @Test
    void shouldExceptionWhenCreateCommentWhenUserNotBooker() {
        UserMapper userMapper = new UserMapper();
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto, ownerDto.getId());
        CommentDto commentDto = new CommentDto(1L, "Comment1", ItemMapper.toItem(itemDto, userMapper.toUser(ownerDto)),
                newUserDto.getName(), LocalDateTime.now());
        ValidationException exp = assertThrows(ValidationException.class,
                () -> itemService.createComment(commentDto, itemDto.getId(), newUserDto.getId()));
        assertEquals("Данный пользователь вещь не бронировал!", exp.getMessage());
    }

    @Test
    void shouldCreateComment() {
        UserMapper userMapper = new UserMapper();
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(3)
        );
        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());
        bookingService.update(bookingDto.getId(), ownerDto.getId(), true);
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CommentDto commentDto = new CommentDto(1L, "Comment1",
                ItemMapper.toItem(itemDto, userMapper.toUser(ownerDto)),
                newUserDto.getName(), LocalDateTime.now());
        itemService.createComment(commentDto, newItemDto.getId(), newUserDto.getId());
        Assertions.assertEquals(1, itemService.getCommentsByItemId(newItemDto.getId()).size());
    }
}