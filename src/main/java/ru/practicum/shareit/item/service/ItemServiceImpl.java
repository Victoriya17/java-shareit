package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotItemOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.OtherItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    public User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public Item findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<OtherItemDto> findAll(Long ownerId) {
        log.debug("Получаем записи о всех вещах пользователя с ID {}", ownerId);

        List<Item> userItems = itemRepository.findAllByOwnerId(ownerId);


        if (userItems.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIds = userItems.stream().map(Item::getId).toList();

        Map<Item, LocalDateTime> lastBookingEnds = bookingRepository
                .findByItemIdInAndStatusAndStartBefore(itemIds, Status.APPROVED, now)
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Booking::getEnd));


        Map<Item, LocalDateTime> nextBookingStarts = bookingRepository
                .findByItemIdInAndStatusAndStartAfter(itemIds, Status.APPROVED, now)
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Booking::getStart));

        Map<Item, List<Comment>> itemsWithComments = commentRepository
                .findByItemIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));

        return userItems.stream()
                .map(item -> {
                    Optional<LocalDateTime> lastEndDate = Optional.ofNullable(lastBookingEnds.get(item));
                    Optional<LocalDateTime> nextStartDate = Optional.ofNullable(nextBookingStarts.get(item));
                    List<Comment> comments = itemsWithComments.getOrDefault(item, Collections.emptyList());

                    return ItemMapper.mapToOtherItemDto(item, comments, lastEndDate, nextStartDate);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto createItem(Long userId, NewItemRequest itemRequest) {
        log.debug("Создание записи о новой вещи {}", itemRequest.getName());
        User user = findUser(userId);

        Item item = ItemMapper.mapToItem(user, itemRequest);
        item = itemRepository.save(item);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, UpdateItemRequest itemRequest, Long ownerId) {
        Item existingItem = findItem(itemId);
        User user = findUser(ownerId);

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotItemOwnerException("Редактировать данные вещи может только её владелец");
        }

        Item updatedItem = ItemMapper.updateItemFields(existingItem, itemRequest);
        updatedItem = itemRepository.save(updatedItem);

        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    @Transactional
    public OtherItemDto findItemById(Long ownerId, Long itemId) {
        log.debug("Поиск вещи c ID{}", itemId);
        Item item = findItem(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        if (!item.getOwner().getId().equals(ownerId)) {
            return ItemMapper.mapToOtherItemDto(item, comments);
        }

        Optional<LocalDateTime> lastBookingEnd = bookingRepository.findLastBookingEndByItemId(itemId, Status.APPROVED,
                        LocalDateTime.now())
                .stream()
                .max(Comparator.naturalOrder());

        Optional<LocalDateTime> nextBookingStart = bookingRepository.findNextBookingStartByItemId(itemId,
                        Status.APPROVED, LocalDateTime.now())
                .stream()
                .min(Comparator.naturalOrder());

        return ItemMapper.mapToOtherItemDto(item, comments, lastBookingEnd, nextBookingStart);
    }

    @Override
    public Collection<ItemDto> findItemToBooking(Long ownerId, String text) {
        log.debug("Получаем записи о вещах, которые ищет арендатор по ключевым символам {}", text);
        User user = findUser(ownerId);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.findItemToBooking(text.toLowerCase())
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long authorId, NewCommentRequest commentRequest) {
        log.debug("Создаем комментарий к вещи");

        User user = findUser(authorId);
        Item item = findItem(itemId);

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(authorId, itemId, LocalDateTime.now())) {
            throw new ValidationException("Пользователь " + user.getName() + " не может оставить комментарий, " +
                    "потому что вещью" + item.getName() + " он не пользовался.");
        }

        Comment comment = CommentMapper.mapToComment(user, item, commentRequest);
        comment = commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }
}
