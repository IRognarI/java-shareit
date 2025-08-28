package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.exception.ItemNotFoundException;
import ru.practicum.shareit.booking.exception.ValidationException;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.service.CheckConsistencyService;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final CheckConsistencyService checker;
    private final BookingService bookingService;
    private final UserService userService;

    @Autowired
    @Lazy
    public ItemServiceImpl(ItemRepository repository, CommentRepository commentRepository,
                           CheckConsistencyService checkConsistencyService,
                           BookingService bookingService,
                           UserService userService) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.checker = checkConsistencyService;
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long id, Long userId) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID=" + id + " не найдена!"));

        List<CommentDto> comments = getCommentsByItemId(id);

        if (userId.equals(item.getOwner().getId())) {

            BookingShortDto lastBooking = bookingService.getLastBooking(id);
            BookingShortDto nextBooking = bookingService.getNextBooking(id);
            return ItemMapper.toItemExtDto(item, lastBooking, nextBooking, comments);
        } else {

            return ItemMapper.toItemDto(item, comments);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Item findItemById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID=" + id + " не найдена!"));
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        checker.isExistUser(ownerId);
        User owner = userService.findUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto, owner);
        Item savedItem = repository.save(item);


        return ItemMapper.toItemDto(savedItem, new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        checker.isExistUser(ownerId);
        return repository.findByOwnerId(ownerId).stream()
                .map(item -> {
                    List<CommentDto> comments = getCommentsByItemId(item.getId());
                    BookingShortDto lastBooking = bookingService.getLastBooking(item.getId());
                    BookingShortDto nextBooking = bookingService.getNextBooking(item.getId());
                    return ItemMapper.toItemExtDto(item, lastBooking, nextBooking, comments);
                })
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(toList());
    }

    @Override
    @Transactional
    public void delete(Long itemId, Long ownerId) {
        try {
            Item item = repository.findById(itemId)
                    .orElseThrow(() -> new ItemNotFoundException("Вещь с ID=" + itemId + " не найдена!"));
            if (!item.getOwner().getId().equals(ownerId)) {
                throw new ItemNotFoundException("У пользователя нет такой вещи!");
            }
            repository.deleteById(itemId);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException("Вещь с ID=" + itemId + " не найдена!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsBySearchQuery(String text) {
        if (text != null && !text.isBlank()) {
            return repository.getItemsBySearchQuery().stream()
                    .filter(it -> it.getName().toLowerCase().contains(text.toLowerCase()) ||
                            it.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .map(item -> {
                        List<CommentDto> comments = getCommentsByItemId(item.getId());
                        return ItemMapper.toItemDto(item, comments);
                    })
                    .collect(toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        checker.isExistUser(ownerId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID=" + itemId + " не найдена!"));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ItemNotFoundException("У пользователя нет такой вещи!");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = repository.save(item);
        List<CommentDto> comments = getCommentsByItemId(itemId);

        return ItemMapper.toItemDto(updatedItem, comments);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        checker.isExistUser(userId);
        Comment comment = new Comment();
        Booking booking = checker.getBookingWithUserBookedItem(itemId, userId);

        if (booking != null) {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(booking.getItem());
            comment.setAuthor(booking.getBooker());
            comment.setText(commentDto.getText());
        } else {
            throw new ValidationException("Данный пользователь вещь не бронировал!");
        }

        Comment savedComment = commentRepository.save(comment);
        return ItemMapper.toCommentDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItem_Id(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(ItemMapper::toCommentDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        // Получаем список элементов по requestId
        List<Item> items = repository.findAllByRequestId(requestId,
                Sort.by(Sort.Direction.DESC, "id"));

        // Для каждого элемента получаем комментарии и мапим в DTO
        return items.stream()
                .map(item -> {
                    // Получаем комментарии для текущего элемента
                    List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                            .map(ItemMapper::toCommentDto)
                            .toList();
                    return ItemMapper.toItemDto(item, comments);
                })
                .collect(Collectors.toList());
    }

}