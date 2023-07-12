package ru.practicum.ewmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.service.*;

import javax.validation.Valid;
import java.util.List;

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
    private final RequestService requestService;

    /**
     * Добавление нового события
     * дата и время на которые намечено событие не может быть раньше,
     * чем через два часа от текущего момента (решено через кастомную аннотацию)
     */
    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public EventFullDto saveEvent(@Valid @RequestBody NewEventDto newEventDto,
                                  @PathVariable Long userId) {
        log.info("Begin of 'POST /users/{userId}/events' Event creation, userId={} for: {}",
                userId, newEventDto.toString());
        return eventService.saveEvent(newEventDto, userId);
    }

    /**
     * Получение событий, добавленных текущим пользователем
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */
    @GetMapping("/{userId}/events")
    public List<EventFullDto> getAllEventsForCurrentUser(@PathVariable Long userId,
                                                         @RequestParam(defaultValue = "0") Long from,
                                                         @RequestParam(defaultValue = "10") Long size) {
        log.info("Begin of 'GET /users/{userId}/events' all Events by userId={}", userId);
        return eventService.getAllEventsForCurrentUser(userId, from, size);
    }

    /**
     * Получение полной информации о событии добавленном текущим пользователем
     * В случае, если события с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByIdForCurrentUser(@PathVariable Long userId,
                                                   @PathVariable Long eventId) {
        log.info("Begin of 'GET /users/{userId}/events/{eventId}' Event by userId={} and eventId={}",
                userId, eventId);
        return eventService.getEventByIdForCurrentUser(userId, eventId);
    }

    /**
     * Изменение события добавленного текущим пользователем,
     * изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
     * дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
     * (Ожидается код ошибки 409)
     */
    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventUserRequest updateEventUserRequest,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId) {
        log.info("Begin of user's 'PATCH /users/{userId}/events/{eventId}' Event by userId={} and eventId={}, new event={}",
                userId, eventId, updateEventUserRequest);
        return eventService.updateEventByUser(updateEventUserRequest, userId, eventId);
    }

    // Private: Запросы на участие. Закрытый API для работы с запросами текущего пользователя на участие в событиях ---

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     * <p>
     * - нельзя добавить повторный запрос (Ожидается код ошибки 409)
     * - инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
     * - нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
     * - если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
     * - если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в
     * состояние подтвержденного
     */
    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public ParticipationRequestDto saveRequest(@PathVariable Long userId,
                                               @RequestParam Long eventId) {
        log.info("Begin of 'POST /users/{userId}/requests' Request creation, userId={}, eventId={}", userId, eventId);
        return requestService.saveRequest(userId, eventId);
    }

    /**
     * Отмена своего запроса на участие в событии
     */
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Begin of 'PATCH /users/{userId}/requests/{requestId}/cancel' Request cancel." +
                " userId={}, requestId={},", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях (Private API)
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getAllRequestsForCurrentUser(@PathVariable Long userId) {
        log.info("Begin of 'GET /users/{userId}/requests' All Requests find for user. userId={}", userId);
        return requestService.getAllRequestsForCurrentUser(userId);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя (Private API)
     * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestsForUsersEvent(@PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        log.info("Begin of 'GET /users/{userId}/requests' All Requests for user's Event. " +
                "userId={}, eventId={}", userId, eventId);
        return requestService.getAllRequestsForUsersEvent(userId, eventId);
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя (Private API)
     * <p>
     * - если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
     * - нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
     * - статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
     * - если при подтверждении данной заявки, лимит заявок для события исчерпан,
     * то все неподтверждённые заявки необходимо отклонить
     */
    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatus(@Valid @RequestBody
                                                                     EventRequestStatusUpdateRequest requestsStatus,
                                                                     @PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        log.info("Begin of 'PATCH /users/{userId}/events/{eventId}/requests' update Requests status. " +
                "userId={}, eventId={}, EventRequestStatusUpdateRequest={}", userId, eventId, requestsStatus.toString());
        return requestService.updateRequestsStatus(userId, eventId, requestsStatus);
    }









}
