package ru.practicum.shareit.request;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.NewRequest;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(long userId, NewRequest request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> findAllItemRequests(@Nullable String pathPart, Long requestorId, Integer from,
                                                      Integer size) {
        String path = (pathPart != null ? pathPart : "") + "?from={from}&size={size}";

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get(path, requestorId, parameters);
    }

    public ResponseEntity<Object> findRequestById(Long requestId) {
        return get("/" + requestId);
    }
}
