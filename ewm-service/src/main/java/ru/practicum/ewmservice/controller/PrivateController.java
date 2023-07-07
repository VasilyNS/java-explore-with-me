package ru.practicum.ewmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.service.EventService;

import javax.validation.Valid;

/**
 * Контроллер закрытой части API, доступно только авторизованным пользователям
 * Закрытая часть начинается на /users
 */
@Slf4j
@RestController
@Validated
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class PrivateController {

    // Private: События - Закрытый API для работы с событиями ------------------------------------

    private final EventService eventService;

    /**
     * Добавление нового события
     * дата и время на которые намечено событие не может быть раньше,
     * чем через два часа от текущего момента (решено через кастомную аннотацию)
     */
    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public EventFullDto saveEvent(@Valid @RequestBody NewEventDto newEventDto,
                                  @PathVariable Long userId) {
        log.info("Begin of 'POST /users/userId/events' Event creation, userId={} for: {}",
                userId, newEventDto.toString());
        return eventService.saveEvent(newEventDto, userId);
    }


    // GET /users/{userId}/events
    // Получение событий, добавленных текущим пользователем
    // В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список

    // ...............

    //

    /**
     * Получение полной информации о событии добавленном текущим пользователем
     * В случае, если события с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public EventFullDto getEventByIdForCurrentUser(@PathVariable Long userId,
                                                   @PathVariable Long eventId) {
        log.info("Begin of 'GET /users/{userId}/events/{eventId}' Event by userId={} and eventId={}",
                userId, eventId);
        return eventService.getEventByIdForCurrentUser(userId, eventId);
    }


}
