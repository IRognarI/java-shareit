package ru.practicum.shareit.item.interfaces;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;

/**
 * В данном интерфейсе объявлены методы для основных операций с сущностью Item:
 * 1. Добавление
 * 2. Обновление
 * 3. Получение по ID
 * 4. Получение всех вещей пользователя
 * 5. Поиск вещи по ключевым словам.
 */

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getUserItem(Long userId);

    List<ItemDto> searchItem(Long userId, String text);
}
