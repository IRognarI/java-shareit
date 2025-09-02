package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.checker.CheckConsistencyService;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.interfaces.ItemRequestService;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository repository;

    @Mock
    private CheckConsistencyService checker;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    private ItemRequestService itemRequestService;

    private final UserDto userDto =
            new UserDto(1L, "Alex", "alex@alex.ru");

    private final ItemRequestDto itemRequestDto =
            new ItemRequestDto(1L, "ItemRequest description",
                    userDto, LocalDateTime.of(2022, 1, 2, 3, 4, 5), null);

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(repository, checker, userService, itemService);
    }

    @Test
    void shouldThrowExceptionWhenGetItemRequestWithWrongId() {
        when(checker.isExistUser(any(Long.class))).thenReturn(true);
        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        final ItemRequestNotFoundException exception = Assertions.assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(-1L, 1L)
        );

        Assertions.assertEquals("Запрос с ID=-1 не найден!", exception.getMessage());
    }
}