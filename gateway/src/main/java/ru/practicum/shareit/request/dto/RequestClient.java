package ru.practicum.shareit.request.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(RequestDto itemRequestDto, Long userId) {

        return post("", userId,itemRequestDto);
    }

    public ResponseEntity<Object> getAllOwnRequests(Long userId) {
        return get("",userId);

    }

    public ResponseEntity<Object> getAllUserRequests(Long userId, Integer from, Integer size) {
        if (from == null && size == null) {
            return get("/all", userId);
        } else {
            Map<String, Object> parameters = Map.of("from", from, "size", size);
            return get("/all?from={from}&size={size}", userId, parameters);
        }

    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {

return get("/" + requestId, userId);
    }
}
