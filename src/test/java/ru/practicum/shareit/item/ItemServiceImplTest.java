package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.exception.ItemNotFoundException;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.service.CheckConsistencyService;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository mockItemRepository;

    @Mock
    private ItemMapper mapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CheckConsistencyService checker;

    @Mock
    private BookingService bookingService;

    @Mock
    UserService userService;

    @Test
    void shouldExceptionWhenGetItemWithWrongId() {
        ItemService itemService = new ItemServiceImpl(mockItemRepository, commentRepository, checker,
                bookingService, userService);
        when(mockItemRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getItemById(-1L, 1L));
        Assertions.assertEquals("Вещь с ID=-1 не найдена!", exception.getMessage());
    }
}