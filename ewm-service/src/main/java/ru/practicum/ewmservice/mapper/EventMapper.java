package ru.practicum.ewmservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.enums.State;
import ru.practicum.ewmservice.enums.StateAction;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.service.CategoryService;
import ru.practicum.ewmservice.service.UserService;
import ru.practicum.statclient.StatClient;
import ru.practicum.statdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventMapper {

    private final UserService userService;
    private final CategoryService categoryService;
    private final StatClient statClient;

    /**
     * Для создания нового события
     */
    public Event toEventFormNewEventDto(NewEventDto newEventDto, Long userId) { // Только для создания нового события
        Event event = new Event();

        event.setId(0L);
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(categoryService.checkExistAndGetCategory(newEventDto.getCategory())); // Category, из БД
        event.setDescription(newEventDto.getDescription());
        event.setConfirmedRequests(0); // Инициализация при создании нового объект
        event.setCreatedOn(LocalDateTime.now()); // Инициализация при создании нового объект
        event.setEventDate(newEventDto.getEventDate());
        event.setInitiator(userService.checkExistAndGetUser(userId)); // Класс User, чтение из БД
        event.setLon(newEventDto.getLocation().getLon());
        event.setLat(newEventDto.getLocation().getLat());
        event.setPaid(newEventDto.getPaid());
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0); // Значение ограничения участников по умолчанию
        } else {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        }
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setState(State.PENDING);
        event.setTitle(newEventDto.getTitle());
        //event.set(newEventDto.get());

        return event;
    }

    /**
     * Для получения полного JSON о событии
     */
    public EventFullDto toEventFullDtoFromEvent(Event event) { // При получении события в web API в полном DTO
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        // Чтение не из БД, а напрямую из поля объекта event другого объекта (сущности) класса Category
        eventFullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());    // TODO: Хранится ли в базе???
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setEventDate(event.getEventDate());
        // Чтение не из БД, а напрямую из поля объекта event другого объекта (сущности) класса User
        eventFullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        // Чтение из БД количества обращений к эндпоинтам событий вида /events/111
        eventFullDto.setViews(getViewsFromStat(event.getId()));
        //eventFullDto.set(event.get());

        return eventFullDto;
    }

    /**
     * Для получения короткого JSON о событии
     */
    public EventShortDto toeventShortDtoFromEvent(Event event) { // При получении события в web API в полном DTO
        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        // Чтение не из БД, а напрямую из поля объекта event другого объекта (сущности) класса Category
        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());    // TODO: Хранится ли в базе???
        eventShortDto.setEventDate(event.getEventDate());
        // Чтение не из БД, а напрямую из поля объекта event другого объекта (сущности) класса User
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        // Чтение из БД количества обращений к эндпоинтам событий вида /events/111
        eventShortDto.setViews(getViewsFromStat(event.getId()));

        return eventShortDto;
    }

    /**
     * Апдейт события для пользователя (закрытое API)
     */
    public Event toEventFromUpdateEventUserRequest(Event event, UpdateEventUserRequest updateEventUserRequest) {
        // Заменяем только если поле не null!
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryService.checkExistAndGetCategory(
                    updateEventUserRequest.getCategory())); // Тут уже читаем из базы, так как может быть новое!
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) { // Объект из 2-х полей
            event.setLat(updateEventUserRequest.getLocation().getLat());
            event.setLon(updateEventUserRequest.getLocation().getLon());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        // С помощью поля stateAction из DTO можно переключить статус в сущности event, но там это поле хранится
        if (updateEventUserRequest.getStateAction() != null) {
            // Если событие было на ревью (PENDING), то устанавливаем статус "снято с ревью" (CANCELED)
            if (updateEventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW) &&
                    event.getState().equals(State.PENDING)) {
                event.setState(State.CANCELED);
            }
            // Если событие было "снято с ревью" (CANCELED), то снова отправлем на ревью (PENDING)
            if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW) &&
                    event.getState().equals(State.CANCELED)) {
                event.setState(State.PENDING);
            }
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        return event;
    }

    /**
     * Апдейт события для админа
     */
    public Event toEventFromUpdateEventAdminRequest(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        // Заменяем только если поле не null!
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryService.checkExistAndGetCategory(
                    updateEventAdminRequest.getCategory())); // Тут уже читаем из базы, так как может быть новое!
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) { // Объект из 2-х полей
            event.setLat(updateEventAdminRequest.getLocation().getLat());
            event.setLon(updateEventAdminRequest.getLocation().getLon());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        // С помощью поля stateAction из DTO можно переключить статус в сущности event, но там это поле хранится
        if (updateEventAdminRequest.getStateAction() != null) {
            // Публикуем
            if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
                // Устанавливаем дату публикации
                event.setPublishedOn(LocalDateTime.now());
            }
            // Отклоняем
            if (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        return event;
    }

    // Читаем статистику по просмотрам event c заданным id
    private Long getViewsFromStat(Long id) {
        LocalDateTime st = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        List<ViewStatsDto> viewStatsDtos = statClient.getStat(st, end, List.of("/events/" + id), false);
        if (viewStatsDtos == null || viewStatsDtos.size() == 0) {
            return 0L;
        } else {
            return viewStatsDtos.get(0).getHits();
        }
    }

}
