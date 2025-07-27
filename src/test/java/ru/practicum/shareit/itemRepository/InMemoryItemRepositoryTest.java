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
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

@RequiredArgsConstructor
public class InMemoryItemRepositoryTest {
    private InMemoryUserRepository userRepository;
    private InMemoryItemRepository inMemoryItemRepository;

    private UserDto testUser1;
    private UserDto testUser2;

    private ItemDto testItem1;
    private ItemDto testItem2;
    private ItemDto testItem3;

    @BeforeEach
    public void setUp() {
        userRepository = new InMemoryUserRepository();
        inMemoryItemRepository = new InMemoryItemRepository(userRepository);

        testUser1 = UserDto.builder().email("fughdfug@mail.ru").name("name#1").build();
        testUser2 = UserDto.builder().email("gri5fggdf@gmail.com").name("name#2").build();

        testItem1 = ItemDto.builder().name("itemName#1").description("some_description#1").available(true).build();
        testItem2 = ItemDto.builder().name("itemName#1_update").description("some_description#1_update").available(false).build();
        testItem3 = ItemDto.builder().name("itemName#3").description("some_description#3").available(false).build();

    }

    @Test
    public void addCorrectItem() {
        UserDto user = userRepository.createUser(testUser1);
        ItemDto item = inMemoryItemRepository.addItem(user.getId(), testItem1);

        Optional<ItemDto> itemDto = Optional.ofNullable(inMemoryItemRepository.getItemById(user.getId(), item.getId()));

        Assertions.assertEquals("itemName#1", itemDto.get().getName());
    }

    @Test
    public void correctUserUpdate() {
        UserDto user = userRepository.createUser(testUser1);
        ItemDto item = inMemoryItemRepository.addItem(user.getId(), testItem1);

        ItemDto updateItem = inMemoryItemRepository.updateItem(user.getId(), item.getId(), testItem2);

        Assertions.assertEquals(updateItem.getOwner(), user.getId());
    }

    @Test
    public void getItemById_mustUndergo_a_check() {
        UserDto user = userRepository.createUser(testUser1);
        ItemDto item = inMemoryItemRepository.addItem(user.getId(), testItem1);

        Optional<ItemDto> itemExists = Optional.ofNullable(inMemoryItemRepository.getItemById(user.getId(), item.getId()));

        Assertions.assertTrue(itemExists.isPresent());
        Assertions.assertEquals(item.getDescription(), itemExists.get().getDescription());
    }

    @Test
    public void getUserItem() {
        UserDto user = userRepository.createUser(testUser1);
        inMemoryItemRepository.addItem(user.getId(), testItem1);
        inMemoryItemRepository.addItem(user.getId(), testItem2);

        List<ItemDto> itemDtoList = inMemoryItemRepository.getUserItem(user.getId());

        Assertions.assertEquals(2, itemDtoList.size());
    }

    @Test
    public void searchItem() {
        UserDto user = userRepository.createUser(testUser1);
        UserDto user2 = userRepository.createUser(testUser2);
        inMemoryItemRepository.addItem(user.getId(), testItem1);
        inMemoryItemRepository.addItem(user.getId(), testItem2);
        inMemoryItemRepository.addItem(user2.getId(), testItem3);

        String text = "name";

        List<ItemDto> itemDtoList = inMemoryItemRepository.searchItem(user.getId(), text);

        Assertions.assertEquals(1, itemDtoList.size());
    }

    /**
     * boundary values
     */

    @Test
    public void unCorrectUpdateItem_userDoesNotExists() {
        UserDto user = userRepository.createUser(testUser1);
        ItemDto item = inMemoryItemRepository.addItem(user.getId(), testItem1);

        Assertions.assertThrows(UserNotFoundException.class, () -> inMemoryItemRepository.updateItem(4L, item.getId(), item));
    }

    @Test
    public void unCorrectUpdateItem_itemDoesNotExists() {
        UserDto user = userRepository.createUser(testUser1);
        ItemDto item = inMemoryItemRepository.addItem(user.getId(), testItem1);

        Assertions.assertThrows(ItemNotFoundException.class, () ->
                inMemoryItemRepository.updateItem(user.getId(), 3L, item));
    }

    @Test
    public void unCorrectUpdateItem_ownerIsNotUpdate() {
        UserDto user = userRepository.createUser(testUser1);
        userRepository.createUser(testUser2);
        ItemDto item = inMemoryItemRepository.addItem(user.getId(), testItem1);

        Assertions.assertThrows(ValidationException.class, () -> inMemoryItemRepository.updateItem(2L, item.getId(), item));
    }

    @Test
    public void getAnOutOfPlace() {
        UserDto user = userRepository.createUser(testUser1);

        Assertions.assertThrows(ItemNotFoundException.class, () -> inMemoryItemRepository.getItemById(user.getId(), 4L));
    }

    @Test
    public void getThingsOfANonExistingUser() {
        userRepository.createUser(testUser1);
        userRepository.createUser(testUser2);

        long userId = 4;
        Assertions.assertThrows(UserNotFoundException.class, () -> inMemoryItemRepository.getUserItem(userId));
    }

    @Test
    public void theDataForSearching() {
        UserDto user = userRepository.createUser(testUser1);

        String text1 = null;
        String text2 = "";

        List<ItemDto> itemDtos1 = inMemoryItemRepository.searchItem(user.getId(), text1);
        List<ItemDto> itemDtos2 = inMemoryItemRepository.searchItem(user.getId(), text2);

        Assertions.assertTrue(itemDtos1.isEmpty() && itemDtos2.isEmpty());
    }
}
