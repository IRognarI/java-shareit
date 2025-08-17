package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long id, Long userId);

    Item findItemById(Long id);

    ItemDto create(ItemDto itemDto, Long ownerId);

    List<ItemDto> getItemsByOwner(Long ownerId);

    void delete(Long itemId, Long ownerId);

    List<ItemDto> getItemsBySearchQuery(String text);

    ItemDto update(ItemDto itemDto, Long ownerId, Long itemId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    List<CommentDto> getCommentsByItemId(Long itemId);
}
