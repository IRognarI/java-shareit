package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.UserIsNotOwnerItemException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.service.CheckConsistencyService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final CheckConsistencyService checker;
    private final ItemService itemService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              CheckConsistencyService checkConsistencyService,
                              ItemService itemService,
                              UserService userService,
                              UserMapper userMapper) {
        this.repository = bookingRepository;
        this.checker = checkConsistencyService;
        this.itemService = itemService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public BookingDto create(BookingInputDto bookingInputDto, Long bookerId) {
        checker.isExistUser(bookerId);

        if (!checker.isAvailableItem(bookingInputDto.getItemId())) {
            throw new ValidationException("Вещь с ID=" + bookingInputDto.getItemId() +
                    " недоступна для бронирования!");
        }

        Item item = itemService.findItemById(bookingInputDto.getItemId());
        User booker = userService.findUserById(bookerId);

        if (bookerId.equals(item.getOwner().getId())) {
            throw new BookingNotFoundException("Вещь с ID=" + bookingInputDto.getItemId() +
                    " недоступна для бронирования самим владельцем!");
        }

        Booking booking = BookingMapper.toBooking(bookingInputDto, item, booker);
        Booking savedBooking = repository.save(booking);


        ItemDto itemDto = itemService.getItemById(item.getId(), bookerId);
        UserDto bookerDto = userMapper.toUserDto(booker);

        return BookingMapper.toBookingDto(savedBooking, itemDto, bookerDto);
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserIsNotOwnerItemException("Подтвердить бронирование может только владелец вещи!");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Решение по бронированию уже принято!");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
            log.info("Владелец с ID={} подтвердил бронирование с ID={}", userId, bookingId);
        } else {
            booking.setStatus(Status.REJECTED);
            log.info("Владелец с ID={} отклонил бронирование с ID={}", userId, bookingId);
        }

        Booking updatedBooking = repository.save(booking);


        ItemDto itemDto = itemService.getItemById(updatedBooking.getItem().getId(), userId);
        UserDto bookerDto = userMapper.toUserDto(updatedBooking.getBooker());

        return BookingMapper.toBookingDto(updatedBooking, itemDto, bookerDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        checker.isExistUser(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));

        if (booking.getBooker().getId().equals(userId) || checker.isItemOwner(booking.getItem().getId(), userId)) {

            ItemDto itemDto = itemService.getItemById(booking.getItem().getId(), userId);
            UserDto bookerDto = userMapper.toUserDto(booking.getBooker());

            return BookingMapper.toBookingDto(booking, itemDto, bookerDto);
        } else {
            throw new UserNotFoundException("Посмотреть данные бронирования может только владелец вещи" +
                    " или бронирующий ее!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookings(String state, Long userId) {
        checker.isExistUser(userId);
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case "ALL":
                bookings = repository.findByBookerId(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = repository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = repository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = repository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "WAITING":
                bookings = repository.findByBookerIdAndStatus(userId, Status.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = repository.findByBookerIdAndStatus(userId, Status.REJECTED, sortByStartDesc);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }


        return bookings.stream()
                .map(booking -> {
                    ItemDto itemDto = itemService.getItemById(booking.getItem().getId(), userId);
                    UserDto bookerDto = userMapper.toUserDto(booking.getBooker());
                    return BookingMapper.toBookingDto(booking, itemDto, bookerDto);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsOwner(String state, Long userId) {
        checker.isExistUser(userId);
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case "ALL":
                bookings = repository.findByItem_Owner_Id(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = repository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = repository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = repository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case "WAITING":
                bookings = repository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = repository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, sortByStartDesc);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }


        return bookings.stream()
                .map(booking -> {
                    ItemDto itemDto = itemService.getItemById(booking.getItem().getId(), userId);
                    UserDto bookerDto = userMapper.toUserDto(booking.getBooker());
                    return BookingMapper.toBookingDto(booking, itemDto, bookerDto);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingShortDto getLastBooking(Long itemId) {
        return BookingMapper.toBookingShortDto(
                repository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BookingShortDto getNextBooking(Long itemId) {
        return BookingMapper.toBookingShortDto(
                repository.findFirstByItem_IdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingWithUserBookedItem(Long itemId, Long userId) {
        return repository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), Status.APPROVED);
    }
}