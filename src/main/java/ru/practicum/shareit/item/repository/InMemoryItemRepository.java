package ru.practicum.shareit.item.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.itemException.ItemNotFoundException;
import ru.practicum.shareit.exception.userException.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.MapToItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class InMemoryItemRepository {
    private final Map<Long, Item> itemMap = new TreeMap<>();
    private final InMemoryUserRepository userRepository;

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        log.info("""
                        \tПолучили id пользователя: {}
                        \tПользователь существует: {}
                        \tПараметры вещи:\tНазвание {}\tОписание {}
                        \tСтатус бронирования {}
                        \tID владельца вещи {}
                        """,
                userId, userExists(userId), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), userId);

        if (!userExists(userId)) {
            throw new UserNotFoundException("Пользователь с id { " + userId + " } - не существует");
        }

        if (!valuesNotNull(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable())) {
            throw new ValidationException("Не достаточно данных для добавления вещи");
        }

        Item item = MapToItem.mapToItem(itemDto);
        Item finalItem = item.toBuilder().id(generatedId()).owner(userId).build();

        itemMap.put(finalItem.getId(), finalItem);

        log.info("\tВернули пользователя {}", finalItem);

        return ItemDto.itemToDto(finalItem);
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("""
                \tПолучили id пользователя {}
                \tID вещи {}
                \tДанные для обновления:
                \tНазвание {}
                \tОписание {}
                \tСтатус {}
                """, userId, itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());

        if (!userExists(userId)) {
            throw new UserNotFoundException("Пользователь с id { " + userId + " } - не существует");
        }

        Optional<Item> itemExists = Optional.ofNullable(itemMap.get(itemId));

        if (itemExists.isEmpty()) {
            throw new ItemNotFoundException("Вещь с id { " + itemId + " } - не добавлена");
        }

        boolean thisIsOwner = itemExists.get().getOwner().equals(userId);

        if (!thisIsOwner) {
            throw new ValidationException("Редактирование разрешено только владельцу");
        }

        Item update = itemExists.get()
                .toBuilder()
                .name(itemDto.getName() != null ?
                        itemDto.getName() : itemExists.get().getName())
                .description(itemDto.getDescription() != null ?
                        itemDto.getDescription() : itemExists.get().getDescription())
                .available(itemDto.getAvailable() != null ?
                        itemDto.getAvailable() : itemExists.get().getAvailable())
                .build();

        log.info("\tВернули пользователя {}", update);

        return ItemDto.itemToDto(update);
    }

    public ItemDto getItemById(Long userId, Long itemId) {
        log.info("\tПолучили ID пользователя {} и вещи {}", userId ,itemId);

        if (!userExists(userId)) {
            throw new UserNotFoundException("Пользователь с id { " + userId + " } - не существует");
        }

        Optional<Item> item = Optional.ofNullable(itemMap.get(itemId));

        if (item.isEmpty()) {
            throw new ItemNotFoundException("Вещь с id { " + itemId + " } - не добавлена");
        }

        log.info("\tВернули {}", item.get());

        return ItemDto.itemToDto(item.get());
    }

    public List<ItemDto> getUserItem(Long userId) {
        log.info("\tПолучили ID пользователя {}", userId);
        log.debug("\tПользователь существует: {}", userExists(userId));

        if (!userExists(userId)) {
            throw new UserNotFoundException("Пользователь с id { " + userId + "} - не найден");
        }

        return itemMap.values()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemDto::itemToDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(Long userId, String text) {
        log.info("\tПолучили ID пользователя {} и текст для поиска {}", userId, text);

        if (!userExists(userId)) {
            throw new UserNotFoundException("Осуществлять поиск могут только авторизированные пользователи");
        }

        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        String textToLoweCase = text.toLowerCase();

        return itemMap.values()
                .stream()
                .filter(item -> {

                    String name = item.getName().toLowerCase();
                    String desc = item.getDescription().toLowerCase();

                    return name.contains(textToLoweCase) || desc.contains(textToLoweCase);

                })
                .filter(item -> item.getAvailable() == true)
                .map(ItemDto::itemToDto)
                .collect(Collectors.toList());
    }

    private boolean userExists(Long userId) {
        UserDto user = userRepository.getUserById(userId);
        return user != null;
    }

    private boolean valuesNotNull(String name, String desc, Boolean available) {
        return (name != null && !name.isEmpty()) && (desc != null && !desc.isEmpty()) && available != null;
    }

    private Long generatedId() {
        long id = itemMap.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        return id + 1;
    }
}
