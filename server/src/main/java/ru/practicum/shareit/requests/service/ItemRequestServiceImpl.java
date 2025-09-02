package ru.practicum.shareit.requests.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.checker.CheckConsistencyService;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.interfaces.ItemRequestService;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final CheckConsistencyService checker;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository repository,
                                  CheckConsistencyService checker,
                                  UserService userService,
                                  ItemService itemService) {
        this.repository = repository;
        this.checker = checker;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created) {

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);


        itemRequest.setRequestor(userService.findUserById(requestorId));
        itemRequest.setCreated(created);


        ItemRequest saved = repository.save(itemRequest);
        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(saved);
        result.setItems(itemService.getItemsByRequestId(saved.getId()));

        return result;
    }

    @Override
    public ItemRequestDto getItemRequestById(Long itemRequestId, Long userId) {
        checker.isExistUser(userId);
        ItemRequest itemRequest = repository.findById(itemRequestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос с ID=" + itemRequestId + " не найден!"));

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);
        dto.setItems(itemService.getItemsByRequestId(itemRequest.getId()));

        return dto;
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long requestorId) {
        checker.isExistUser(requestorId);
        return repository.findAllByRequestorId(requestorId, Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(req -> {
                    ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(req);
                    dto.setItems(itemService.getItemsByRequestId(req.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        checker.isExistUser(userId);
        List<ItemRequestDto> result = new ArrayList<>();
        Pagination pager = new Pagination(from, size);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        if (size == null) {
            List<ItemRequest> list = repository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
            result.addAll(list.stream()
                    .skip(from)
                    .map(req -> {
                        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(req);
                        dto.setItems(itemService.getItemsByRequestId(req.getId()));
                        return dto;
                    }).collect(Collectors.toList()));
        } else {
            for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                Pageable pageable = PageRequest.of(i, pager.getPageSize(), sort);
                Page<ItemRequest> page = repository.findAllByRequestorIdNot(userId, pageable);
                result.addAll(page.stream().map(req -> {
                    ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(req);
                    dto.setItems(itemService.getItemsByRequestId(req.getId()));
                    return dto;
                }).collect(Collectors.toList()));
                if (!page.hasNext()) break;
            }
            result = result.stream().limit(size).collect(Collectors.toList());
        }
        return result;
    }
}