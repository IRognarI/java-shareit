package ru.practicum.shareit.itemRepository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.itemException.ItemNotFoundException;
import ru.practicum.shareit.exception.userException.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
public class ItemRepositoryTest {
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    private UserDto testUser1;
    private UserDto testUser2;

    private ItemDto testItem1;
    private ItemDto testItem2;
    private ItemDto testItem3;

    @BeforeEach
    public void setUp() {
        userRepository = new UserRepository();
        itemRepository = new ItemRepository(userRepository);

        testUser1 = UserDto.builder().email("fughdfug@mail.ru").name("name#1").build();
        testUser2 = UserDto.builder().email("gri5fggdf@gmail.com").name("name#2").build();

        testItem1 = ItemDto.builder().name("itemName#1").description("some_description#1").available(true).build();
        testItem2 = ItemDto.builder().name("itemName#1_update").description("some_description#1_update").available(false).build();
        testItem3 = ItemDto.builder().name("itemName#3").description("some_description#3").available(false).build();

    }

    @Test
    public void addCorrectItem() {
        UserDto user = userRepository.createUser(testUser1);
        ItemDto item = itemRepository.addItem(user.getId(), testItem1);

        Optional<ItemDto> itemDto = Optional.ofNullable(itemRepository.getItemById(user.getId(), item.getId()));

        Assertions.assertEquals("itemName#1", itemDto.get().getName());
    }

    @Test
    public void correctUserUpdate() {
        UserDto user = userRepository.createUser(testUser1);
        ItemDto item = itemRepository.addItem(user.getId(), testItem1);

        ItemDto updateItem = itemRepository.updateItem(user.getId(), item.getId(), testItem2);

        Assertions.assertEquals(updateItem.getOwner(), user.getId());
    }

    @Test
    public void getItemById_mustUndergo_a_check() {
        UserDto user = userRepository.createUser(testUser1);
        ItemDto item = itemRepository.addItem(user.getId(), testItem1);

        Optional<ItemDto> itemExists = Optional.ofNullable(itemRepository.getItemById(user.getId(), item.getId()));

        Assertions.assertTrue(itemExists.isPresent());
        Assertions.assertEquals(item.getDescription(), itemExists.get().getDescription());
    }

    @Test
    public void getUserItem() {
        UserDto user = userRepository.createUser(testUser1);
        itemRepository.addItem(user.getId(), testItem1);
        itemRepository.addItem(user.getId(), testItem2);

        List<ItemDto> itemDtoList = itemRepository.getUserItem(user.getId());

        Assertions.assertEquals(2, itemDtoList.size());
    }

    @Test
    public void searchItem() {
        UserDto user = userRepository.createUser(testUser1);
        UserDto user2 = userRepository.createUser(testUser2);
        itemRepository.addItem(user.getId(), testItem1);
        itemRepository.addItem(user.getId(), testItem2);
        itemRepository.addItem(user2.getId(), testItem3);

        String text = "name";

        List<ItemDto> itemDtoList = itemRepository.searchItem(user.getId(), text);

        Assertions.assertEquals(1, itemDtoList.size());
    }

    /**
     * boundary values
     */

    @Test
    public void unCorrectUpdateItem_userDoesNotExists() {
        UserDto user = userRepository.createUser(testUser1);
        ItemDto item = itemRepository.addItem(user.getId(), testItem1);

        Assertions.assertThrows(UserNotFoundException.class, () -> itemRepository.updateItem(4L, item.getId(), item));
    }

    @Test
    public void unCorrectUpdateItem_itemDoesNotExists() {
        UserDto user = userRepository.createUser(testUser1);
        ItemDto item = itemRepository.addItem(user.getId(), testItem1);

        Assertions.assertThrows(ItemNotFoundException.class, () ->
                itemRepository.updateItem(user.getId(), 3L, item));
    }

    @Test
    public void unCorrectUpdateItem_ownerIsNotUpdate() {
        UserDto user = userRepository.createUser(testUser1);
        userRepository.createUser(testUser2);
        ItemDto item = itemRepository.addItem(user.getId(), testItem1);

        Assertions.assertThrows(ValidationException.class, () -> itemRepository.updateItem(2L, item.getId(), item));
    }

    @Test
    public void getAnOutOfPlace() {
        UserDto user = userRepository.createUser(testUser1);

        Assertions.assertThrows(ItemNotFoundException.class, () -> itemRepository.getItemById(user.getId(), 4L));
    }

    @Test
    public void getThingsOfANonExistingUser() {
        userRepository.createUser(testUser1);
        userRepository.createUser(testUser2);

        long userId = 4;
        Assertions.assertThrows(UserNotFoundException.class, () -> itemRepository.getUserItem(userId));
    }

    @Test
    public void theDataForSearching() {
        UserDto user = userRepository.createUser(testUser1);

        String text1 = null;
        String text2 = "";

        List<ItemDto> itemDtos1 = itemRepository.searchItem(user.getId(), text1);
        List<ItemDto> itemDtos2 = itemRepository.searchItem(user.getId(), text2);

        Assertions.assertTrue(itemDtos1.isEmpty() && itemDtos2.isEmpty());
    }
}
