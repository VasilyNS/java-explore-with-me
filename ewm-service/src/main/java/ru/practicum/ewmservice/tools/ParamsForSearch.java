package ru.practicum.ewmservice.tools;

import lombok.*;

import java.util.List;

/**
 * Класс для передачи параметров для поиска записей в БД для эндпоинтов
 */
@Data
@Builder
public class ParamsForSearch {
    private List<Long> users;
    private List<String> states;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;
    private Long from;
    private Long size;

    private String text;
    private Boolean paid;
    private Boolean onlyAvailable;
    private String sort; // Available values : EVENT_DATE, VIEWS
}
