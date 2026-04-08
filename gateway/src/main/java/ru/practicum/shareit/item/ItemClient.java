package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long userId, NewItemRequest itemRequest) {
        return post("", userId, itemRequest);
    }

    public ResponseEntity<Object> updateItem(Long itemId, UpdateItemRequest itemRequest, Long ownerId) {
        return patch("/" + itemId, ownerId, itemRequest);
    }

    public ResponseEntity<Object> findItemById(Long ownerId, Long itemId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> findAll(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findItemToBooking(Long ownerId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );

        return get("/search?text={text}&from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> addComment(Long itemId, Long authorId, NewCommentRequest commentRequest) {
        return post("/" + itemId + "/comment", authorId, commentRequest);
    }
}
