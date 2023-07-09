package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.enums.State;
import ru.practicum.ewmservice.enums.StateAction;
import ru.practicum.ewmservice.mapper.EventMapper;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.tools.exception.NotFoundException;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventFullDto saveEvent(NewEventDto newEventDto, Long userId) {
        Event event = eventMapper.toEventFormNewEventDto(newEventDto, userId);

        Event eventForReturn = eventRepository.save(event);
        return eventMapper.toEventFullDtoFromEvent(eventForReturn);
    }

    @Transactional
    public EventFullDto updateEventByUser(UpdateEventUserRequest updateEventUserRequest, Long userId, Long eventId) {
        Event event = checkExistAndGetEvent(eventId);

        // Если была попытка изменить чужое событие
        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("Event " + eventId);
        }
        // Изменить можно только отмененные события или события в состоянии ожидания модерации
        if (!(event.getState().equals(State.CANCELED) || event.getState().equals(State.PENDING))) {
            throw new DataIntegrityViolationException("Only pending or canceled events can be changed"); // Код 409
        }

        // Редактируем все поля, которые пришли в updateEventUserRequest и не null
        event = eventMapper.toEventFromUpdateEventUserRequest(event, updateEventUserRequest);

        Event eventForReturn = eventRepository.save(event);
        return eventMapper.toEventFullDtoFromEvent(eventForReturn);
    }

    @Transactional
    public EventFullDto updateEventByAdmin(UpdateEventAdminRequest updateEventAdminRequest, Long eventId) {
        Event event = checkExistAndGetEvent(eventId);

        if (updateEventAdminRequest.getStateAction() != null) {

            // Проверка условия: дата начала изменяемого события должна быть не ранее чем
            // за 1 час от даты публикации. (Ожидается код ошибки 409)
            // 1) Проверка что этот апдейт на публикацию
            if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                // 2) Изначально берем дату из ивента в базе
                LocalDateTime checkDate = event.getEventDate();
                // 3) Если дата события пришла в dto апдейта, то берем её
                if (updateEventAdminRequest.getEventDate() != null) {
                    checkDate = updateEventAdminRequest.getEventDate();
                }
                LocalDateTime checkDateMinusTime = checkDate.minusHours(1);
                // 4) Проверка на разницу менее 1 часа и выброс исключения
                if (checkDateMinusTime.isBefore(LocalDateTime.now())) {
                    throw new DataIntegrityViolationException("The start date of the event to be changed must "
                            + "be no earlier than 1 hour from the date of publication"); // Код 409
                }

                // Проверка условия: событие можно публиковать, только если оно в состоянии ожидания публикации (-> 409)
                if (!event.getState().equals(State.PENDING)) {
                    throw new DataIntegrityViolationException("The event can be published only if it is "
                            + "pending publication"); // Код 409
                }
            }

            // Проверка условия: событие можно отклонить, только если оно еще не опубликовано (Иначе искл. 409)
            if (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT)) {
                if (event.getState().equals(State.PUBLISHED)) {
                    throw new DataIntegrityViolationException("The event can only be rejected if it has not "
                            + "yet been published"); // Код 409
                }
            }
        }

        // Редактируем все поля, которые пришли в updateEventUserRequest и не null
        event = eventMapper.toEventFromUpdateEventAdminRequest(event, updateEventAdminRequest);

        Event eventForReturn = eventRepository.save(event);
        return eventMapper.toEventFullDtoFromEvent(eventForReturn);
    }


    @Transactional(readOnly = true)
    public EventFullDto getEventByIdForCurrentUser(Long userId, Long eventId) {
        Event event = checkExistAndGetEvent(eventId);
        if (event.getInitiator().getId() != userId) { // Если была попытка смотреть подробности чужого события:
            throw new NotFoundException("Event " + eventId);
        }
        return eventMapper.toEventFullDtoFromEvent(event);
    }

    public EventFullDto getEventByIdForPublicApi(Long id) {
        Event event = eventRepository.getEventByIdForPublicApi(State.PUBLISHED, id)
                .orElseThrow(() -> new NotFoundException("Event " + id));
        return eventMapper.toEventFullDtoFromEvent(event);
    }



    /**
     * Проверка, что сущность есть в БД, если нет - исключение, если да - возврат объекта с ней
     */
    public Event checkExistAndGetEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event " + id));
    }

}
