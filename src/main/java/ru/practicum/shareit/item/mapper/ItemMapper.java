package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {

    // Метод для базового преобразования (без бронирований)
    public static ItemDto toItemDto(Item item, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId(),
                null, // lastBooking - null по умолчанию
                null, // nextBooking - null по умолчанию
                comments // Используем переданный список комментариев
        );
    }

    // Метод для преобразования с данными о бронированиях (для владельца)
    public static ItemDto toItemExtDto(Item item, BookingShortDto lastBooking,
                                       BookingShortDto nextBooking, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId(),
                lastBooking, // Используем переданное последнее бронирование
                nextBooking, // Используем переданное следующее бронирование
                comments // Используем переданный список комментариев
        );
    }

    // Метод для преобразования DTO в Entity. Принимает готовую сущность User.
    public static Item toItem(ItemDto itemDto, User owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner, // Используем переданного владельца
                itemDto.getRequestId()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(), // Берем имя напрямую из сущности
                comment.getCreated()
        );
    }
}