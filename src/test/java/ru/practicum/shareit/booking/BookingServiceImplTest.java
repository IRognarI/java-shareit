package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.checker.CheckConsistencyService;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private CheckConsistencyService checker;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Test
    void shouldExceptionWhenGetBookingWithWrongId() {
        BookingService bookingService = new BookingServiceImpl(repository, checker, itemService, userService, userMapper);
        when(checker.isExistUser(any(Long.class)))
                .thenReturn(true);
        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getBookingById(-1L, 1L));
        Assertions.assertEquals("Бронирование с ID=-1 не найдено!", exception.getMessage());
    }
}