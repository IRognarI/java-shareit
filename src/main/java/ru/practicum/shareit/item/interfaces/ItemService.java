package ru.practicum.shareit.item.interfaces;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getUserItem(Long userId);

    List<ItemDto> searchItem(Long userId, String text);
}
