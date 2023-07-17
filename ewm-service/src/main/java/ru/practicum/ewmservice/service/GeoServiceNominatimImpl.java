package ru.practicum.ewmservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.tools.Const;
import ru.practicum.statclient.BaseClient;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GeoServiceNominatimImpl extends BaseClient implements GeoService {

    /**
     * ObjectMapper является "тяжелым" потокобезопасным объектом и может быть переиспользован.
     * Чтобы не создавать новые объекты, он вынесен как поле класса.
     * При масштабировании приложения объект целесообразно вынести в отдельный конфигурационный класс.
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    public GeoServiceNominatimImpl(@Value("${statserver.url}") String serverUrl) {
        super(Const.GEO_SERVICE_NOMINATIM);
    }

    public GeoDto getPlaceName(Float lat, Float lon) {
        GeoDto geoDto = new GeoDto();

        // Упаковываем все параметры в мапу
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("lat", lat);
        parameters.put("lon", lon);
        parameters.put("format", "json");

        // Отправляем запрос и получаем ответ, ошибки обработает ErrorHandler
        ResponseEntity<Object> response;
        response = get("/reverse", parameters);

        // Проверка, что ответ имеет код успеха 2xx
        if (response.getStatusCode().is2xxSuccessful()) {
            // Тело ответа должно содержать json со списком объектов,
            // вытаскиваем его в список dto-объектов Java через ObjectMapper
            GeoNominatimDto geoNominatimDto = objectMapper.convertValue(
                    response.getBody(),
                    new TypeReference<GeoNominatimDto>() {
                    }
            );
            geoDto.setName(geoNominatimDto.getDisplayName());
            return geoDto;
        } else {
            log.warn("Error in server response: {}", response.getStatusCode());
            return null;
        }

    }

}