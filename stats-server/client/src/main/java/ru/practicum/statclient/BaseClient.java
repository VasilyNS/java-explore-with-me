package ru.practicum.statclient;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

public class BaseClient {

    protected final RestTemplate rest;
    private final String serverUrl;

    public BaseClient(String serverUrl) {

        this.serverUrl = serverUrl;

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

        this.rest = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> serverResponse;
        try {
            if (parameters != null) {

                // Получение URI из эндпоинта и Map из параметров,
                // причем элементом мапы могут быть массивы
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(path);
                for (String key : parameters.keySet()) {
                    Object o = parameters.get(key);
                    if (o instanceof List) {
                        // Требуется явное преобразование объекта в тип список объектов
                        List<Object> l = (List<Object>) o;
                        for (Object elem : l) {
                            builder.queryParam(key, elem);
                        }
                    } else {
                        builder.queryParam(key, o);
                    }
                }
                String pathAndParams = builder.build().toUriString();

                serverResponse = rest.exchange(pathAndParams, method, requestEntity, Object.class);
            } else {
                serverResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(serverResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

}
