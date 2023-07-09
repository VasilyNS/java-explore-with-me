package ru.practicum.ewmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.CategoryDto;
import ru.practicum.ewmservice.dto.EventFullDto;
import ru.practicum.ewmservice.service.CategoryService;
import ru.practicum.ewmservice.service.EventService;
import ru.practicum.statclient.StatClient;
import ru.practicum.statdto.StatDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер публичной части API, доступно без регистрации любому пользователю сети
 */
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class PublicController {

    private final CategoryService categoryService;
    private final EventService eventService;
    private final StatClient statClient;

    // Public: Категории - Публичный API для работы с категориями ------------------------------------

    /**
     * В случае, если по заданным фильтрам не найдено ни одной категории, возвращает пустой список
     */
    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") Long from,
                                              @RequestParam(defaultValue = "10") Long size) {
        log.info("Begin of 'GET /categories' All categories getting");

        return categoryService.getAllCategories(from, size);
    }

    /**
     * В случае, если категории с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/categories/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        log.info("Begin of 'GET /categories/id' category getting by id={}", id);

        return categoryService.getCategoryById(id);
    }

    // Public: События. Публичный API для работы с событиями ------------------------------------

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору
     * <p>
     * - событие должно быть опубликовано
     * - информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
     * - информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     * В случае, если события с заданным id не найдено, возвращает статус код 404.
     */
    @GetMapping("/events/{id}")
    public EventFullDto getEventByIdForPublicApi(@PathVariable Long id, HttpServletRequest request) {
        log.info("Begin of 'GET /events/{id}' Event for public API eventId={}", id);

        // Для этого эндпоинта необходимо отправить статистику на сервер статистики через клента
        StatDto statDto = new StatDto("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());
        statClient.saveStat(statDto);

        return eventService.getEventByIdForPublicApi(id);
    }


}
