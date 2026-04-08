package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody NewItemRequest itemRequest) {
        return itemClient.createItem(userId, itemRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @Valid @RequestBody UpdateItemRequest itemRequest,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.updateItem(itemId, itemRequest, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @PathVariable Long itemId) {
        return itemClient.findItemById(ownerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.findAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                  @RequestParam(name = "text", defaultValue = "") String text,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.findItemToBooking(ownerId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long authorId,
                                             @Valid @RequestBody NewCommentRequest commentRequest) {
        return itemClient.addComment(itemId, authorId, commentRequest);
    }
}
