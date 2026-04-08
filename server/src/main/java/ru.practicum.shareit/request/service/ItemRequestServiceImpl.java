package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, NewRequest request) {
        log.debug("Создание нового запроса на вещь {}", request.getDescription());
        User requestor = findUser(userId);

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(request, requestor);
        itemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> findAllItemRequestsByRequestorId(Long requestorId) {
        log.debug("Получение списка запросов пользователя с ID {}", requestorId);
        User requestor = findUser(requestorId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);
        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.mapToItemRequestDto(request);
                    List<Item> items = itemRepository.findByRequestId(request.getId());
                    dto.setItems(items.stream()
                            .map(item -> {
                                ItemDto itemDto = new ItemDto();
                                itemDto.setId(item.getId());
                                itemDto.setName(item.getName());
                                itemDto.setOwnerId(item.getOwner().getId());
                                return itemDto;
                            })
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> findAllItemRequests(Long requestorId) {
        log.debug("Получение списка запросов вещей, отсортированных по дате создания (новые сначала)");
        return itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(requestorId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto findRequestById(Long requestId) {
        log.debug("Поиск запроса c ID {}", requestId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + requestId + " не найден"));

        List<Item> items = itemRepository.findAllByRequestId(requestId);

        return ItemRequestMapper.mapToItemRequestDto(request, items);
    }
}
