package ru.practicum.statclient;

import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.statdto.StatDto;
import ru.practicum.statdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatClient extends BaseClient {

    /**
     * ObjectMapper является "тяжелым" потокобезопасным объектом и может быть переиспользован.
     * Чтобы не создавать новые объекты, он вынесен как поле класса.
     * При масштабировании приложения объект целесообразно вынести в отдельный конфигурационный класс.
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    public StatClient(@Value("${statserver.url}") String serverUrl) {
        super(serverUrl);
    }

    public void saveStat(StatDto statDto) {
        // Ошибки обработает ErrorHandler
        post("/hit", statDto);
    }

    public List<ViewStatsDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Упаковываем все параметры в мапу
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(formatter));
        parameters.put("end", "qqq" /*end.format(formatter)*/);
        parameters.put("unique", unique);
        if (uris != null && uris.size() != 0) {
            parameters.put("uris", uris);
        }

        // Отправляем запрос и получаем ответ, ошибки обработает ErrorHandler
        ResponseEntity<Object> response;
        response = get("/stats", parameters);

        // Проверка, что ответ имеет код успеха 2xx
        if (response.getStatusCode().is2xxSuccessful()) {
            // Тело ответа должно содержать json со списком объектов,
            // вытаскиваем его в список dto-объектов Java через ObjectMapper
            List<ViewStatsDto> viewStatsDtos = objectMapper.convertValue(
                    response.getBody(),
                    new TypeReference<List<ViewStatsDto>>() {
                    }
            );
            return viewStatsDtos;
        } else {
            log.warn("Error in server response: {}", response.getStatusCode());
            return null;
        }
    }

}