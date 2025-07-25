package ru.practicum.shareit.item.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("ID пользователя не может быть " + userId);
        }

        if (itemDto == null) {
            throw new ValidationException("Не достаточно данных для добавления вещи");
        }

        return repository.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("ID пользователя не может быть " + userId);
        }

        if (itemId == null || itemId <= 0) {
            throw new ValidationException("ID вещи не может быть " + itemId);
        }

        if (itemDto == null) {
            throw new ValidationException("Не достаточно данных для обновления вещи");
        }

        return repository.updateItem(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        if (itemId == null || itemId <= 0) {
            throw new ValidationException("ID вещи не может быть " + itemId);
        }

        return repository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getUserItem(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("ID пользователя не может быть " + userId);
        }

        return repository.getUserItem(userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isEmpty()) {
            throw new ValidationException("Укажите больше данных для поиска");
        }

        return repository.searchItem(text);
    }
}
