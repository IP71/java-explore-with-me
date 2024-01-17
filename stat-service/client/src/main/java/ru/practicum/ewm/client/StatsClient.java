package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import ru.practicum.ewm.model.EndpointHitDto;

import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    @Autowired
    public StatsClient(@Value("http://stats-server:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addEndpointHit(String app, String uri, String ip, String timestamp) {
        return post("/hit", EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp)
                .build());
    }

    public ResponseEntity<Object> getStats(String start, String end, String[] uris, boolean unique) {
        if (uris == null) {
            Map<String, Object> parameters = Map.of(
                    "start", start,
                    "end", end,
                    "unique", unique);
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        } else {
            Map<String, Object> parameters = Map.of(
                    "start", start,
                    "end", end,
                    "uris", uris,
                    "unique", unique);
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        }
    }

    public Boolean checkIfIpIsUnique(String uri, String ip) {
        Map<String, Object> parameters = Map.of(
                "uri", uri,
                "ip", ip);
        return Boolean.parseBoolean(get("/unique?uri={uri}&ip={ip}", parameters).getBody().toString());
    }
}
